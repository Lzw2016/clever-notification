package org.clever.notification.rabbit.consumer;

import lombok.extern.slf4j.Slf4j;
import org.clever.notification.config.RabbitBeanConfig;
import org.clever.notification.model.EmailMessage;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 18:14 <br/>
 */
@RabbitListener(queues = {RabbitBeanConfig.EmailMessageQueue})
@Component
@Slf4j
public class SendEmailNotification {

    @RabbitHandler
    public void send(EmailMessage emailMessage) {
        try {
            log.info("### 发送邮件 {}", emailMessage);
        } catch (Throwable e) {
            // TODO 异步通知失败
            log.info("### 发送邮件失败 {}", emailMessage.getId());
            throw e;
        }
        // TODO 异步通知成功

        // 抛出异常 Nack
//        throw new BusinessException("发送失败");
        //手动ACK
//        channel.basicAck(deliveryTag, false);
//        channel.basicNack(deliveryTag, false, false);
    }
}
