package org.clever.notification.rabbit.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.config.RabbitBeanConfig;
import org.clever.notification.model.EmailMessage;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 18:14 <br/>
 */
@RabbitListener(queues = {RabbitBeanConfig.EmailMessageQueue})
@Component
@Slf4j
public class SendEmailNotification {

    @RabbitHandler
    public void send(EmailMessage emailMessage, Message message, Channel channel) throws IOException {
        try {
            Thread.sleep(1000 * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("### 发送邮件 {}", emailMessage);
        //手动ACK
        channel.basicAck((long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG), false);
    }
}
