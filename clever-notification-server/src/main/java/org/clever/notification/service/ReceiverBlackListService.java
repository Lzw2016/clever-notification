package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.ReceiverBlackList;
import org.clever.notification.mapper.ReceiverBlackListMapper;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.IExcludeBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ReceiverBlackListMapper receiverBlackListMapper;

    @SuppressWarnings("ConstantConditions")
    @PostConstruct
    @Transactional
    public synchronized void load() {
        // TODO 批量操作Redis
        int enabledCount = receiverBlackListMapper.updateEnabledByExpiredTime();
        List<ReceiverBlackList> receiverBlackLists = receiverBlackListMapper.findAllEnabled();
        for (ReceiverBlackList receiverBlackList : receiverBlackLists) {
            String key;
            if (StringUtils.isBlank(receiverBlackList.getSysName())) {
                // {KeyPrefix}:{message_type}:{account}
                key = String.format("%s:%s:%s", KeyPrefix, receiverBlackList.getMessageType(), receiverBlackList.getAccount());
            } else {
                // {KeyPrefix}:{sys_name}:{message_type}:{account}
                key = String.format("%s:%s:%s:%s", KeyPrefix, receiverBlackList.getSysName(), receiverBlackList.getMessageType(), receiverBlackList.getAccount());
            }
            if (receiverBlackList.getExpiredTime() != null) {
                long timeout = receiverBlackList.getExpiredTime().getTime() - System.currentTimeMillis();
                if (timeout > 0) {
                    // 设置数据过期时间
                    redisTemplate.opsForValue().set(key, receiverBlackList.getAccount(), timeout, TimeUnit.MILLISECONDS);
                }
            } else {
                // 不设置数据过期时间
                redisTemplate.opsForValue().set(key, receiverBlackList.getAccount());
            }
        }
        log.info("### 加载黑名单 加载黑名单数量:{} 禁用黑名单数量:{}", receiverBlackLists.size(), enabledCount);
    }

    /**
     * 帐号是否在黑名单列表中
     */
    @Override
    public boolean inBlackList(String sysName, Integer messageType, String account) {
        // TODO 需要判断两次不好需要优化
        // 先找当前系统黑名单
        // {KeyPrefix}:{sys_name}:{message_type}:{account}
        String key = String.format("%s:%s:%s:%s", KeyPrefix, sysName, messageType, account);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            // 再找全局黑名单
            // {KeyPrefix}:{message_type}:{account}
            key = String.format("%s:%s:%s", KeyPrefix, messageType, account);
            value = redisTemplate.opsForValue().get(key);
            return value != null;
        }
        return false;
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
