package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.clever.notification.rabbit.producer.IDistinctSendId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-07 14:17 <br/>
 */
@Component
@Slf4j
public class DistinctSendIdService implements IDistinctSendId {

    /**
     * 黑名单Redis key前缀
     */
    private static final String SendIdKey = "clever-notification:send-id";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean existsSendId(long sendId) {
        Boolean result = redisTemplate.opsForSet().isMember(SendIdKey, String.valueOf(sendId));
        // 使用Redis或者数据库去重 Message SendId
        return result != null && result;
    }

    @Override
    public void addSendId(long sendId) {
        redisTemplate.opsForSet().add(SendIdKey, String.valueOf(sendId));
    }
}
