package org.clever.notification.rabbit.producer;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.SnowFlake;
import org.clever.notification.config.RabbitBeanConfig;
import org.clever.notification.model.EmailMessage;
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
public class SendEmailMessage {

    @Autowired
    private SnowFlake snowFlake;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     */
    public EmailMessage send(EmailMessage emailMessage) {
        emailMessage.setId(snowFlake.nextId());
        rabbitTemplate.convertAndSend(
                RabbitBeanConfig.MessageExchange,
                String.format("%s.%s", RabbitBeanConfig.EmailRoutingKey, emailMessage.getSysName()),
                emailMessage,
                new CorrelationData(emailMessage.getId().toString())
        );
        return emailMessage;
    }
}
