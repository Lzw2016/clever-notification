package org.clever.notification.send.rabbit.producer;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.SnowFlake;
import org.clever.notification.model.SmsMessage;
import org.clever.notification.send.BaseSendMessage;
import org.clever.notification.send.IDistinctSendId;
import org.clever.notification.send.IExcludeBlackList;
import org.clever.notification.send.IFrequencyLimit;
import org.clever.notification.send.rabbit.RabbitConfig;
import org.clever.notification.service.SendSmsService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
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
    @Autowired
    private IExcludeBlackList excludeBlackList;
    @Autowired
    private IFrequencyLimit frequencyLimit;
    @Autowired
    private IDistinctSendId distinctSendId;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SendSmsService sendSmsService;

    @Override
    protected Long nextId() {
        return snowFlake.nextId();
    }

    @Override
    protected IExcludeBlackList getIExcludeBlackList() {
        return excludeBlackList;
    }

    @Override
    protected IFrequencyLimit getIFrequencyLimit() {
        return frequencyLimit;
    }

    @Override
    protected IDistinctSendId getIDistinctSendId() {
        return distinctSendId;
    }

    @Override
    protected void internalAsyncSend(SmsMessage smsMessage) {
        // 异步发送短信
        rabbitTemplate.convertAndSend(
                RabbitConfig.ExchangeName,
                RabbitConfig.getRoutingKey(RabbitConfig.Sms, smsMessage.getSysName()),
                smsMessage,
                new CorrelationData(smsMessage.getSendId().toString())
        );
    }

    @Override
    protected void internalSend(SmsMessage smsMessage) {
        log.info("### 同步发送短信 {}", smsMessage.getSendId());
        if (sendSmsService.sendSms(smsMessage)) {
            log.info("### 同步发送短信 [成功] {}", smsMessage.getSendId());
        } else {
            log.info("### 同步发送短信 [失败] {}", smsMessage.getSendId());
        }
    }
}
