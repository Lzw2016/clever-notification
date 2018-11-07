package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.rabbit.producer.IFrequencyLimit;
import org.springframework.stereotype.Service;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-07 14:06 <br/>
 */
@Service
@Slf4j
public class FrequencyLimitService implements IFrequencyLimit {

    @Override
    public boolean frequencyLimit(Integer messageType, String account) {
        return false;
    }

    @Override
    public <T extends BaseMessage> T removeFrequencyLimit(T message) {
        return message;
    }
}
