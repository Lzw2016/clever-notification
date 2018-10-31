package org.clever.notification.rabbit.consumer;

import lombok.extern.slf4j.Slf4j;
import org.clever.notification.config.RabbitBeanConfig;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.service.SendEmailService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 18:14 <br/>
 */
@RabbitListener(queues = {RabbitBeanConfig.EmailMessageQueue})
@Component
@Slf4j
public class SendEmailNotification {

    @Autowired
    private SendEmailService sendEmailService;

    @RabbitHandler
    public void send(EmailMessage emailMessage) {
        log.info("### 处理发送邮件 {}", emailMessage.getSendId());
        try {
            // TODO 使用Redis或者数据库去重 MessageID
            if (sendEmailService.senEmail(emailMessage)) {
                // TODO 异步通知成功
            } else {
                // TODO 异步通知失败
            }
        } catch (Throwable e) {
            log.error("### 处理发送邮件失败 {}", emailMessage.getSendId());
            // TODO 异步通知失败
            // TODO 记录或通知当前错误
            // 抛出异常 Nack
            throw e;
        }
    }
}
