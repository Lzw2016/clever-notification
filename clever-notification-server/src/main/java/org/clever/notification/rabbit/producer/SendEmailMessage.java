package org.clever.notification.rabbit.producer;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.SnowFlake;
import org.clever.notification.config.RabbitBeanConfig;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.BaseSendMessage;
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

    @Override
    protected Long nextId() {
        return snowFlake.nextId();
    }

    @Override
    protected void internalAsyncSend(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend(
                RabbitBeanConfig.MessageExchange,
                String.format("%s.%s", RabbitBeanConfig.EmailRoutingKey, emailMessage.getSysName()),
                emailMessage,
                new CorrelationData(emailMessage.getSendId().toString())
        );
    }

    @Override
    protected void internalSend(EmailMessage emailMessage) {
        log.info("### 同步发送邮件 {}", emailMessage.getSendId());
        sendEmailService.senEmail(emailMessage);
        log.info("### 同步发送邮件 [成功] {}", emailMessage.getSendId());
    }
}
