package org.clever.notification.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.FrequencyLimit;
import org.clever.notification.mapper.FrequencyLimitMapper;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.IFrequencyLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-07 14:06 <br/>
 */
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
    private static final String frequencyLimitSet = "clever-notification:frequency-limit-config-set";

    /**
     * 所有频率限制配置 临时Key
     */
    private static final String frequencyLimitSetTmp = "clever-notification:frequency-limit-config-set-tmp";

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
                String key;
                // TODO 判断空不是非空
                if (StringUtils.isNotBlank(frequencyLimit.getAccount()) && frequencyLimit.getMessageType() != null) {
                    // {ConfigKeyPrefix}:{sys_name}:{message_type}:{account}
                    key = String.format("%s:%s:%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName(), frequencyLimit.getMessageType(), frequencyLimit.getAccount());
                } else if (frequencyLimit.getMessageType() != null) {
                    // {ConfigKeyPrefix}:{sys_name}:{message_type}
                    key = String.format("%s:%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName(), frequencyLimit.getMessageType());
                } else {
                    // {ConfigKeyPrefix}:{sys_name}
                    key = String.format("%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName());
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
                String key;
                // TODO 判断空不是非空
                if (StringUtils.isNotBlank(frequencyLimit.getAccount()) && frequencyLimit.getMessageType() != null) {
                    // {ConfigKeyPrefix}:{sys_name}:{message_type}:{account}
                    key = String.format("%s:%s:%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName(), frequencyLimit.getMessageType(), frequencyLimit.getAccount());
                } else if (frequencyLimit.getMessageType() != null) {
                    // {ConfigKeyPrefix}:{sys_name}:{message_type}
                    key = String.format("%s:%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName(), frequencyLimit.getMessageType());
                } else {
                    // {ConfigKeyPrefix}:{sys_name}
                    key = String.format("%s:%s", ConfigKeyPrefix, frequencyLimit.getSysName());
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
                    connection.setNX(key.getBytes(), value.getBytes());
                }
            }
            return null;
        });
        log.info("### 加载发送频率配置数量:{} | 删除的发送频率配置数量：{} | 禁用发送频率配置数量:{}", frequencyLimits.size(), keySet.size(), enabledCount);
    }

    @Override
    public boolean frequencyLimit(String sysName, Integer messageType, String account) {
        // TODO 判断是否频率超限
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
            // TODO connection.exists在Pipelined中无效
            for (String account : accountSet) {
                // {ConfigKeyPrefix}:{sys_name}:{message_type}:{account}
                // {ConfigKeyPrefix}:{sys_name}:{message_type}
                // {ConfigKeyPrefix}:{sys_name}
                String key = String.format("%s:%s:%s:%s", ConfigKeyPrefix, emailMessage.getSysName(), EnumConstant.MessageType_1, account);
                Boolean exists = connection.exists(key.getBytes());
                if (exists == null || !exists) {
                    key = String.format("%s:%s:%s", ConfigKeyPrefix, emailMessage.getSysName(), EnumConstant.MessageType_1);
                    exists = connection.exists(key.getBytes());
                    if (exists == null || !exists) {
                        key = String.format("%s:%s", ConfigKeyPrefix, emailMessage.getSysName());
                        exists = connection.exists(key.getBytes());
                        if (exists == null || !exists) {
                            continue;
                        }
                    }
                }
                // {KeyPrefix}:{sys_name}:{message_type}:{account}:minutes-count
                // {KeyPrefix}:{sys_name}:{message_type}:{account}:hours-count
                // {KeyPrefix}:{sys_name}:{message_type}:{account}:days-count
                // {KeyPrefix}:{sys_name}:{message_type}:{account}:weeks-count
                // {KeyPrefix}:{sys_name}:{message_type}:{account}:months-count
                String minutesKey = String.format("%s:%s:%s:%s:minutes-count", KeyPrefix, emailMessage.getSysName(), EnumConstant.MessageType_1, account);
                String hoursKey = String.format("%s:%s:%s:%s:hours-count", KeyPrefix, emailMessage.getSysName(), EnumConstant.MessageType_1, account);
                String daysKey = String.format("%s:%s:%s:%s:days-count", KeyPrefix, emailMessage.getSysName(), EnumConstant.MessageType_1, account);
                String weeksKey = String.format("%s:%s:%s:%s:weeks-count", KeyPrefix, emailMessage.getSysName(), EnumConstant.MessageType_1, account);
                String monthsKey = String.format("%s:%s:%s:%s:months-count", KeyPrefix, emailMessage.getSysName(), EnumConstant.MessageType_1, account);
                exists = connection.exists(minutesKey.getBytes());
                if (exists == null || !exists) {
                    connection.setEx(minutesKey.getBytes(), MinuteBySecond, "1".getBytes());
                } else {
                    connection.incr(minutesKey.getBytes());
                }
                exists = connection.exists(hoursKey.getBytes());
                if (exists == null || !exists) {
                    connection.setEx(hoursKey.getBytes(), HourBySecond, "1".getBytes());
                } else {
                    connection.incr(hoursKey.getBytes());
                }
                exists = connection.exists(daysKey.getBytes());
                if (exists == null || !exists) {
                    connection.setEx(daysKey.getBytes(), DayBySecond, "1".getBytes());
                } else {
                    connection.incr(daysKey.getBytes());
                }
                exists = connection.exists(weeksKey.getBytes());
                if (exists == null || !exists) {
                    connection.setEx(weeksKey.getBytes(), WeekBySecond, "1".getBytes());
                } else {
                    connection.incr(weeksKey.getBytes());
                }
                exists = connection.exists(monthsKey.getBytes());
                if (exists == null || !exists) {
                    connection.setEx(monthsKey.getBytes(), MonthBySecond, "1".getBytes());
                } else {
                    connection.incr(monthsKey.getBytes());
                }
            }
            return null;
        });
        return emailMessage;
    }

    @NoArgsConstructor
    @Data
    public static class FrequencyLimitCount implements Serializable {
        /**
         * 一分钟内的发送次数(小于等于0表示不限制)
         */
        private Integer minutesCount;

        /**
         * 一小时内的发送次数(小于等于0表示不限制)
         */
        private Integer hoursCount;

        /**
         * 一天内的发送次数(小于等于0表示不限制)
         */
        private Integer daysCount;

        /**
         * 一周内的发送次数(小于等于0表示不限制)
         */
        private Integer weeksCount;

        /**
         * 一月内的发送次数(小于等于0表示不限制)
         */
        private Integer monthsCount;

        FrequencyLimitCount(FrequencyLimit frequencyLimit) {
            minutesCount = frequencyLimit.getMinutesCount();
            hoursCount = frequencyLimit.getHoursCount();
            daysCount = frequencyLimit.getDaysCount();
            weeksCount = frequencyLimit.getWeeksCount();
            monthsCount = frequencyLimit.getMonthsCount();
        }
    }
}
