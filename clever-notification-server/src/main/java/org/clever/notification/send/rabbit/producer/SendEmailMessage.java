package org.clever.notification.send.rabbit.producer;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.SnowFlake;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.send.BaseSendMessage;
import org.clever.notification.send.IDistinctSendId;
import org.clever.notification.send.IExcludeBlackList;
import org.clever.notification.send.IFrequencyLimit;
import org.clever.notification.send.rabbit.RabbitConfig;
import org.clever.notification.service.SendEmailService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 17:15 <br/>
 */
@Component
@Slf4j
public class SendEmailMessage extends BaseSendMessage<EmailMessage> {

    @Autowired
    private SnowFlake snowFlake;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private IExcludeBlackList excludeBlackList;
    @Autowired
    private IFrequencyLimit frequencyLimit;
    @Autowired
    private IDistinctSendId distinctSendId;

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
    protected void internalAsyncSend(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.ExchangeName,
                RabbitConfig.getRoutingKey(RabbitConfig.Email, emailMessage.getSysName()),
                emailMessage,
                new CorrelationData(emailMessage.getSendId().toString())
        );
    }

    @Override
    protected void internalSend(EmailMessage emailMessage) {
        log.info("### 同步发送邮件 {}", emailMessage.getSendId());
        if (sendEmailService.sendEmail(emailMessage)) {
            log.info("### 同步发送邮件 [成功] {}", emailMessage.getSendId());
        } else {
            log.info("### 同步发送邮件 [失败] {}", emailMessage.getSendId());
        }
    }
}
