package org.clever.notification.rabbit.producer.impl;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.SnowFlake;
import org.clever.notification.model.SmsMessage;
import org.clever.notification.rabbit.producer.BaseSendMessage;
import org.clever.notification.rabbit.producer.IDistinctSendId;
import org.clever.notification.rabbit.producer.IExcludeBlackList;
import org.clever.notification.rabbit.producer.IFrequencyLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 21:51 <br/>
 */
@Component
@Slf4j
public class SendSmsMessage extends BaseSendMessage<SmsMessage> {

    @Autowired
    private SnowFlake snowFlake;

    @Override
    protected Long nextId() {
        return snowFlake.nextId();
    }

    @Override
    protected IExcludeBlackList getIExcludeBlackList() {
        return null;
    }

    @Override
    protected IFrequencyLimit getIFrequencyLimit() {
        return null;
    }

    @Override
    protected IDistinctSendId getIDistinctSendId() {
        return null;
    }

    @Override
    protected void internalAsyncSend(SmsMessage baseMessage) {
        // TODO 异步发送短信
    }

    @Override
    protected void internalSend(SmsMessage baseMessage) {
        // TODO 同步发送短信
    }
}
