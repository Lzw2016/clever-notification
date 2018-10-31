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
        log.info("### 处理发送邮件 {} 通知地址 -> {}", emailMessage.getSendId(), emailMessage.getAsyncCallBack());
        try {
            // TODO 使用Redis或者数据库去重 MessageID
            // TODO 黑名单限制
            // TODO 发送频率限制
            if (sendEmailService.senEmail(emailMessage)) {
                // TODO 异步通知成功
                log.info("### 处理发送邮件 [成功] {}", emailMessage.getSendId());
            } else {
                // TODO 异步通知失败
                log.info("### 处理发送邮件 [失败] {}", emailMessage.getSendId());
            }
        } catch (Throwable e) {
            log.error("### 处理发送邮件[错误] {}", emailMessage.getSendId());
            // TODO 异步通知失败
            // TODO 记录或通知当前错误
            // 抛出异常 Nack
            throw e;
        }
    }
}
