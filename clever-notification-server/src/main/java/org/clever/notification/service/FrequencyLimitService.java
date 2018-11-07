package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.exception.BusinessException;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.FrequencyLimit;
import org.clever.notification.mapper.FrequencyLimitMapper;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.IFrequencyLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
     * 黑名单Redis key前缀
     */
    private static final String KeyPrefix = "clever-notification:frequency-limit";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private FrequencyLimitMapper frequencyLimitMapper;

    @PostConstruct
    @Transactional
    public synchronized void load() {
//        {KeyPrefix}:{sys_name}
//        {KeyPrefix}:{sys_name}:{message_type}
//        {KeyPrefix}:{sys_name}:{message_type}:{account}
        // 求差集 TODO 频率限制
        Set<String> keySet = new HashSet<>();
        // 查询所有黑名单
        int enabledCount = frequencyLimitMapper.updateEnabledByExpiredTime();
        List<FrequencyLimit> receiverBlackLists = frequencyLimitMapper.findAllEnabled();
        log.info("### 加载发送频率配置数量:{} | 删除的发送频率配置数量：{} | 禁用发送频率配置数量:{}", receiverBlackLists.size(), keySet.size(), enabledCount);
    }

    @Override
    public boolean frequencyLimit(String sysName, Integer messageType, String account) {
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
}
