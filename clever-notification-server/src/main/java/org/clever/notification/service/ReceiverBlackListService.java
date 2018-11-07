package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.exception.BusinessException;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.ReceiverBlackList;
import org.clever.notification.mapper.ReceiverBlackListMapper;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.IExcludeBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 18:16 <br/>
 */
@Service
@Slf4j
public class ReceiverBlackListService implements IExcludeBlackList {

    /**
     * 黑名单Redis key前缀
     */
    private static final String KeyPrefix = "clever-notification:black-list";

    /**
     * 当前所有黑名单 Key
     */
    private static final String blackListSet = "clever-notification:black-list-set";

    /**
     * 所有黑名单 临时Key
     */
    private static final String blackListSetTmp = "clever-notification:black-list-set-tmp";


    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ReceiverBlackListMapper receiverBlackListMapper;

    @SuppressWarnings("ConstantConditions")
    @PostConstruct
    @Transactional
    public synchronized void load() {
        // 初始化 blackListSet blackListSetTmp
        if (!redisTemplate.hasKey(blackListSet)) {
            Set<String> set = redisTemplate.keys(KeyPrefix + ":*");
            log.info("### set ={}", set.size());
            if (set.size() <= 0) {
                set.add("");
            }
            redisTemplate.opsForSet().add(blackListSet, set.toArray(new String[]{}));
        }
        if (!redisTemplate.hasKey(blackListSetTmp)) {
            redisTemplate.opsForSet().add(blackListSetTmp, "");
        }
        // 查询所有黑名单
        int enabledCount = receiverBlackListMapper.updateEnabledByExpiredTime();
        List<ReceiverBlackList> receiverBlackLists = receiverBlackListMapper.findAllEnabled();
        // 删除当前不存在的数据
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (ReceiverBlackList receiverBlackList : receiverBlackLists) {
                // {KeyPrefix}:{sys_name}:{message_type}:{account}
                String key = String.format(
                        "%s:%s:%s:%s",
                        KeyPrefix,
                        receiverBlackList.getSysName(),
                        receiverBlackList.getMessageType(),
                        receiverBlackList.getAccount()
                );
                connection.sAdd(blackListSetTmp.getBytes(), key.getBytes());
            }
            return null;
        });
        // 求差集
        Set<String> keySet = redisTemplate.opsForSet().difference(blackListSet, blackListSetTmp);
        redisTemplate.delete(keySet);
        // 替换 blackListSetTmp -> blackListSet
        redisTemplate.delete(blackListSet);
        redisTemplate.rename(blackListSetTmp, blackListSet);
        // 插入所有的黑名单数据
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (ReceiverBlackList receiverBlackList : receiverBlackLists) {
                // {KeyPrefix}:{sys_name}:{message_type}:{account}
                String key = String.format(
                        "%s:%s:%s:%s",
                        KeyPrefix,
                        receiverBlackList.getSysName(),
                        receiverBlackList.getMessageType(),
                        receiverBlackList.getAccount()
                );
                if (receiverBlackList.getExpiredTime() != null) {
                    long timeout = (receiverBlackList.getExpiredTime().getTime() - System.currentTimeMillis()) / 1000;
                    if (timeout > 0) {
                        // 设置数据过期时间
                        connection.setEx(key.getBytes(), timeout, receiverBlackList.getAccount().getBytes());
                    }
                } else {
                    // 不设置数据过期时间
                    connection.setNX(key.getBytes(), receiverBlackList.getAccount().getBytes());
                }
            }
            return null;
        });
        log.info("### 加载黑名单数量:{} | 删除的黑名单数量：{} | 禁用黑名单数量:{}", receiverBlackLists.size(), keySet.size(), enabledCount);
    }

    /**
     * 帐号是否在黑名单列表中
     */
    @Override
    public boolean inBlackList(String sysName, Integer messageType, String account) {
        // {KeyPrefix}:{sys_name}:{message_type}:{account}
        final String key = String.format("%s:%s:%s:%s", KeyPrefix, sysName, messageType, account);
        final String globalKey = String.format("%s:%s:%s:%s", KeyPrefix, EnumConstant.RootSysName, messageType, account);
        List<String> values = redisTemplate.opsForValue().multiGet(new ArrayList<String>() {{
            add(key);
            add(globalKey);
        }});
        assert values != null;
        String valueAccount = values.stream().filter(Objects::nonNull).findFirst().orElse(null);
        return Objects.equals(account, valueAccount);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BaseMessage> T removeBlackList(T message) {
        if (message instanceof EmailMessage) {
            EmailMessage emailMessage = removeBlackList((EmailMessage) message);
            return (T) emailMessage;
        } else {
            throw new BusinessException("不支持的消息类型: " + message.getClass().getName());
        }
    }

    private EmailMessage removeBlackList(EmailMessage emailMessage) {
        Set<String> accountSet = new HashSet<>(emailMessage.getTo());
        if (emailMessage.getCc() != null) {
            accountSet.addAll(emailMessage.getCc());
        }
        if (emailMessage.getBcc() != null) {
            accountSet.addAll(emailMessage.getBcc());
        }
        Set<String> removeAccount = new HashSet<>();
        for (String account : accountSet) {
            if (inBlackList(emailMessage.getSysName(), EnumConstant.MessageType_1, account)) {
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
            throw new BusinessException("过滤黑名单之后没有消息接收者");
        }
        return emailMessage;
    }
}
