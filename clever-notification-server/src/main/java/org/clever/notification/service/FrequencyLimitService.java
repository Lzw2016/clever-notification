package org.clever.notification.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.FrequencyLimit;
import org.clever.notification.mapper.FrequencyLimitMapper;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.IFrequencyLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-07 14:06 <br/>
 */
@SuppressWarnings("Duplicates")
@Service
@Slf4j
public class FrequencyLimitService implements IFrequencyLimit {

    /**
     * 频率限制配置 key前缀
     */
    private static final String ConfigKeyPrefix = "clever-notification:frequency-limit-config";

    /**
     * 当前所有频率限制配置 Key
     */
    private static final String frequencyLimitSet = "clever-notification:frequency-limit-config:set";

    /**
     * 所有频率限制配置 临时Key
     */
    private static final String frequencyLimitSetTmp = "clever-notification:frequency-limit-config:set-tmp";

    /**
     * 账号发送频率数据 key前缀
     */
    private static final String KeyPrefix = "clever-notification:frequency-limit";

    private static final long MinuteBySecond = 60;
    private static final long HourBySecond = 60 * MinuteBySecond;
    private static final long DayBySecond = 24 * HourBySecond;
    private static final long WeekBySecond = 7 * DayBySecond;
    private static final long MonthBySecond = 30 * DayBySecond;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private FrequencyLimitMapper frequencyLimitMapper;

    @SuppressWarnings({"ConstantConditions", "Duplicates"})
    @PostConstruct
    @Transactional
    public synchronized void load() {
        // 初始化 frequencyLimitSet frequencyLimitSetTmp
        if (!redisTemplate.hasKey(frequencyLimitSet)) {
            Set<String> set = redisTemplate.keys(ConfigKeyPrefix + ":*");
            if (set.size() <= 0) {
                set.add("");
            }
            redisTemplate.opsForSet().add(frequencyLimitSet, set.toArray(new String[]{}));
        }
        if (!redisTemplate.hasKey(frequencyLimitSetTmp)) {
            redisTemplate.opsForSet().add(frequencyLimitSetTmp, "");
        }
        // 查询所有黑名单
        int enabledCount = frequencyLimitMapper.updateEnabledByExpiredTime();
        List<FrequencyLimit> frequencyLimits = frequencyLimitMapper.findAllEnabled();
        // 删除当前不存在的数据
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (FrequencyLimit frequencyLimit : frequencyLimits) {
                String key = getConfigKey(frequencyLimit);
                if (StringUtils.isBlank(key)) {
                    frequencyLimitMapper.deleteById(frequencyLimit.getId());
                    log.warn("### 删除无效配置 {}", frequencyLimit);
                    continue;
                }
                connection.sAdd(frequencyLimitSetTmp.getBytes(), key.getBytes());
            }
            return null;
        });
        // 求差集
        Set<String> keySet = redisTemplate.opsForSet().difference(frequencyLimitSet, frequencyLimitSetTmp);
        assert keySet != null;
        redisTemplate.delete(keySet);
        // 替换 blackListSetTmp -> blackListSet
        redisTemplate.delete(frequencyLimitSet);
        redisTemplate.rename(frequencyLimitSetTmp, frequencyLimitSet);
        // 插入所有的黑名单数据
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (FrequencyLimit frequencyLimit : frequencyLimits) {
                String key = getConfigKey(frequencyLimit);
                if (StringUtils.isBlank(key)) {
                    frequencyLimitMapper.deleteById(frequencyLimit.getId());
                    log.warn("### 删除无效配置 {}", frequencyLimit);
                    continue;
                }
                String value = JacksonMapper.nonEmptyMapper().toJson(new FrequencyLimitCount(frequencyLimit));
                if (frequencyLimit.getExpiredTime() != null) {
                    long timeout = (frequencyLimit.getExpiredTime().getTime() - System.currentTimeMillis()) / 1000;
                    if (timeout > 0) {
                        // 设置数据过期时间
                        connection.setEx(key.getBytes(), timeout, value.getBytes());
                    }
                } else {
                    // 不设置数据过期时间
                    connection.set(key.getBytes(), value.getBytes());
                }
            }
            return null;
        });
        log.info("### 加载发送频率配置数量:{} | 删除的发送频率配置数量：{} | 禁用发送频率配置数量:{}", frequencyLimits.size(), keySet.size(), enabledCount);
    }

    private String getConfigKey(FrequencyLimit frequencyLimit) {
        // {ConfigKeyPrefix}:{sys_name}:{message_type}:{account}
        // {ConfigKeyPrefix}:{sys_name}:{message_type}
        // {ConfigKeyPrefix}:{sys_name}
        String key;
        if (StringUtils.isBlank(frequencyLimit.getAccount()) && frequencyLimit.getMessageType() == null) {
            key = String.format("%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName());
        } else if (StringUtils.isBlank(frequencyLimit.getAccount()) && frequencyLimit.getMessageType() != null) {
            key = String.format("%s:%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName(), frequencyLimit.getMessageType());
        } else if (StringUtils.isNotBlank(frequencyLimit.getAccount()) && frequencyLimit.getMessageType() != null) {
            key = String.format("%s:%s:%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName(), frequencyLimit.getMessageType(), frequencyLimit.getAccount());
        } else {
            key = null;
        }
        return key;

    }

    @Override
    public boolean frequencyLimit(String sysName, Integer messageType, String account) {
        // 判断是否频率超限
        FrequencyLimitCount frequencyLimitCount = existsConfig(sysName, messageType, account);
        if (frequencyLimitCount == null) {
            return false;
        }
        if (frequencyLimitCount.getMinutesCount() <= 0
                && frequencyLimitCount.getHoursCount() <= 0
                && frequencyLimitCount.getDaysCount() <= 0
                && frequencyLimitCount.getWeeksCount() <= 0
                && frequencyLimitCount.getMonthsCount() <= 0) {
            return false;
        }
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:minutes-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:hours-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:days-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:weeks-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:months-count
        final String minutesKey = String.format("%s:%s:%s:%s:minutes-count", KeyPrefix, sysName, messageType, account);
        final String hoursKey = String.format("%s:%s:%s:%s:hours-count", KeyPrefix, sysName, messageType, account);
        final String daysKey = String.format("%s:%s:%s:%s:days-count", KeyPrefix, sysName, messageType, account);
        final String weeksKey = String.format("%s:%s:%s:%s:weeks-count", KeyPrefix, sysName, messageType, account);
        final String monthsKey = String.format("%s:%s:%s:%s:months-count", KeyPrefix, sysName, messageType, account);
        List<String> keyList = new ArrayList<String>() {{
            add(minutesKey);
            add(hoursKey);
            add(daysKey);
            add(weeksKey);
            add(monthsKey);
        }};
        List<String> countList = redisTemplate.opsForValue().multiGet(keyList);
        assert countList != null;
        for (int i = 0; i < countList.size(); i++) {
            if ((i == 0 && frequencyLimitCount.getMinutesCount() <= 0)
                    || (i == 1 && frequencyLimitCount.getHoursCount() <= 0)
                    || (i == 2 && frequencyLimitCount.getDaysCount() <= 0)
                    || (i == 3 && frequencyLimitCount.getWeeksCount() <= 0)
                    || (i == 4 && frequencyLimitCount.getMonthsCount() <= 0)) {
                continue;
            }
            int count = NumberUtils.toInt(countList.get(i), -1);
            if (count <= 0) {
                continue;
            }
            switch (i) {
                case 0:
                    if (frequencyLimitCount.getMinutesCount() <= count) {
                        log.info(
                                "### 发送频率频率超限 [系统:{} | 消息类型:{} | 账号:{}] [每分钟最大发送次数:{} | 当前次数:{}]",
                                sysName, messageType, account, frequencyLimitCount.getMinutesCount(), count + 1
                        );
                        return true;
                    }
                    break;
                case 1:
                    if (frequencyLimitCount.getHoursCount() <= count) {
                        log.info(
                                "### 发送频率频率超限 [系统:{} | 消息类型:{} | 账号:{}] [每小时最大发送次数:{} | 当前次数:{}]",
                                sysName, messageType, account, frequencyLimitCount.getHoursCount(), count + 1
                        );
                        return true;
                    }
                    break;
                case 2:
                    if (frequencyLimitCount.getDaysCount() <= count) {
                        log.info(
                                "### 发送频率频率超限 [系统:{} | 消息类型:{} | 账号:{}] [每天最大发送次数:{} | 当前次数:{}]",
                                sysName, messageType, account, frequencyLimitCount.getDaysCount(), count + 1
                        );
                        return true;
                    }
                    break;
                case 3:
                    if (frequencyLimitCount.getWeeksCount() <= count) {
                        log.info(
                                "### 发送频率频率超限 [系统:{} | 消息类型:{} | 账号:{}] [每周最大发送次数:{} | 当前次数:{}]",
                                sysName, messageType, account, frequencyLimitCount.getWeeksCount(), count + 1
                        );
                        return true;
                    }
                    break;
                case 4:
                    if (frequencyLimitCount.getMonthsCount() <= count) {
                        log.info(
                                "### 发送频率频率超限 [系统:{} | 消息类型:{} | 账号:{}] [每月最大发送次数:{} | 当前次数:{}]",
                                sysName, messageType, account, frequencyLimitCount.getMonthsCount(), count + 1
                        );
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BaseMessage> T removeFrequencyLimit(T message) {
        if (message instanceof EmailMessage) {
            EmailMessage emailMessage = removeFrequencyLimit((EmailMessage) message);
            return (T) emailMessage;
        } else {
            throw new BusinessException("不支持的消息类型: " + message.getClass().getName());
        }
    }

    private EmailMessage removeFrequencyLimit(EmailMessage emailMessage) {
        Set<String> accountSet = new HashSet<>(emailMessage.getTo());
        if (emailMessage.getCc() != null) {
            accountSet.addAll(emailMessage.getCc());
        }
        if (emailMessage.getBcc() != null) {
            accountSet.addAll(emailMessage.getBcc());
        }
        Set<String> removeAccount = new HashSet<>();
        for (String account : accountSet) {
            if (frequencyLimit(emailMessage.getSysName(), EnumConstant.MessageType_1, account)) {
                removeAccount.add(account);
            }
        }
        emailMessage.getTo().removeAll(removeAccount);
        if (emailMessage.getCc() != null) {
            emailMessage.getCc().removeAll(removeAccount);
        }
        if (emailMessage.getBcc() != null) {
            emailMessage.getBcc().removeAll(removeAccount);
        }
        if (emailMessage.getTo().size() <= 0) {
            throw new BusinessException("发送频率限制之后没有消息接收者");
        }
        return emailMessage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BaseMessage> T addFrequency(T message) {
        if (message instanceof EmailMessage) {
            EmailMessage emailMessage = addFrequency((EmailMessage) message);
            return (T) emailMessage;
        } else {
            throw new BusinessException("不支持的消息类型: " + message.getClass().getName());
        }
    }

    private EmailMessage addFrequency(EmailMessage emailMessage) {
        Set<String> accountSet = new HashSet<>(emailMessage.getTo());
        if (emailMessage.getCc() != null) {
            accountSet.addAll(emailMessage.getCc());
        }
        if (emailMessage.getBcc() != null) {
            accountSet.addAll(emailMessage.getBcc());
        }
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (String account : accountSet) {
                FrequencyLimitCount frequencyLimitCount = existsConfig(emailMessage.getSysName(), EnumConstant.MessageType_1, account);
                addFrequency(connection, frequencyLimitCount, emailMessage.getSysName(), EnumConstant.MessageType_1, account);
            }
            return null;
        });
        return emailMessage;
    }

    /**
     * 返回存在的限速配置
     */
    private FrequencyLimitCount existsConfig(String sysName, Integer messageType, String account) {
        FrequencyLimitCount frequencyLimitCount = new FrequencyLimitCount();
        // {ConfigKeyPrefix}:{sys_name}
        // {ConfigKeyPrefix}:{sys_name}:{message_type}
        // {ConfigKeyPrefix}:{sys_name}:{message_type}:{account}
        List<String> configList = redisTemplate.opsForValue().multiGet(new ArrayList<String>() {{
            add(String.format("%s:%s", ConfigKeyPrefix, sysName));
            add(String.format("%s:%s:%s", ConfigKeyPrefix, sysName, messageType));
            add(String.format("%s:%s:%s:%s", ConfigKeyPrefix, sysName, messageType, account));
        }});
        assert configList != null;
        for (String config : configList) {
            if (StringUtils.isNotBlank(config)) {
                FrequencyLimitCount tmp = JacksonMapper.nonEmptyMapper().fromJson(config, FrequencyLimitCount.class);
                frequencyLimitCount.merge(tmp);
            }
        }
        return frequencyLimitCount;
    }

    /**
     * 增加发送频率
     */
    @SuppressWarnings("Duplicates")
    private void addFrequency(RedisConnection connection, FrequencyLimitCount frequencyLimitCount, String sysName, Integer messageType, String account) {
        if (connection == null || frequencyLimitCount == null) {
            return;
        }
        if (frequencyLimitCount.getMinutesCount() <= 0
                && frequencyLimitCount.getHoursCount() <= 0
                && frequencyLimitCount.getDaysCount() <= 0
                && frequencyLimitCount.getWeeksCount() <= 0
                && frequencyLimitCount.getMonthsCount() <= 0) {
            return;
        }
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:minutes-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:hours-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:days-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:weeks-count
        // {KeyPrefix}:{sys_name}:{message_type}:{account}:months-count
        final String minutesKey = String.format("%s:%s:%s:%s:minutes-count", KeyPrefix, sysName, messageType, account);
        final String hoursKey = String.format("%s:%s:%s:%s:hours-count", KeyPrefix, sysName, messageType, account);
        final String daysKey = String.format("%s:%s:%s:%s:days-count", KeyPrefix, sysName, messageType, account);
        final String weeksKey = String.format("%s:%s:%s:%s:weeks-count", KeyPrefix, sysName, messageType, account);
        final String monthsKey = String.format("%s:%s:%s:%s:months-count", KeyPrefix, sysName, messageType, account);
        List<String> keyList = new ArrayList<String>() {{
            add(minutesKey);
            add(hoursKey);
            add(daysKey);
            add(weeksKey);
            add(monthsKey);
        }};
        List<Long> secondsList = new ArrayList<Long>() {{
            add(MinuteBySecond);
            add(HourBySecond);
            add(DayBySecond);
            add(WeekBySecond);
            add(MonthBySecond);
        }};
        List<String> countList = redisTemplate.opsForValue().multiGet(keyList);
        assert countList != null;
        for (int i = 0; i < countList.size(); i++) {
            if ((i == 0 && frequencyLimitCount.getMinutesCount() <= 0)
                    || (i == 1 && frequencyLimitCount.getHoursCount() <= 0)
                    || (i == 2 && frequencyLimitCount.getDaysCount() <= 0)
                    || (i == 3 && frequencyLimitCount.getWeeksCount() <= 0)
                    || (i == 4 && frequencyLimitCount.getMonthsCount() <= 0)) {
                continue;
            }
            String key = keyList.get(i);
            String count = countList.get(i);
            if (StringUtils.isBlank(count)) {
                connection.setEx(key.getBytes(), secondsList.get(i), "1".getBytes());
            } else {
                connection.incr(key.getBytes());
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class FrequencyLimitCount implements Serializable {
        /**
         * 一分钟内的发送次数(小于等于0表示不限制)
         */
        private int minutesCount = -1;

        /**
         * 一小时内的发送次数(小于等于0表示不限制)
         */
        private int hoursCount = -1;

        /**
         * 一天内的发送次数(小于等于0表示不限制)
         */
        private int daysCount = -1;

        /**
         * 一周内的发送次数(小于等于0表示不限制)
         */
        private int weeksCount = -1;

        /**
         * 一月内的发送次数(小于等于0表示不限制)
         */
        private int monthsCount = -1;

        FrequencyLimitCount(FrequencyLimit frequencyLimit) {
            minutesCount = frequencyLimit.getMinutesCount();
            hoursCount = frequencyLimit.getHoursCount();
            daysCount = frequencyLimit.getDaysCount();
            weeksCount = frequencyLimit.getWeeksCount();
            monthsCount = frequencyLimit.getMonthsCount();
        }

        /**
         * 合并限速配置
         */
        void merge(FrequencyLimitCount frequencyLimitCount) {
            if (frequencyLimitCount == null) {
                return;
            }
            if (minutesCount <= 0) {
                minutesCount = frequencyLimitCount.minutesCount;
            }
            if (hoursCount <= 0) {
                hoursCount = frequencyLimitCount.hoursCount;
            }
            if (daysCount <= 0) {
                daysCount = frequencyLimitCount.daysCount;
            }
            if (weeksCount <= 0) {
                weeksCount = frequencyLimitCount.weeksCount;
            }
            if (monthsCount <= 0) {
                monthsCount = frequencyLimitCount.monthsCount;
            }
        }
    }
}
