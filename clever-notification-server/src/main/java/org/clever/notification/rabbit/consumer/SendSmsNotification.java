package org.clever.notification.rabbit.consumer;

import lombok.extern.slf4j.Slf4j;
import org.clever.notification.config.RabbitBeanConfig;
import org.clever.notification.model.SmsMessage;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 21:53 <br/>
 */
@RabbitListener(queues = {RabbitBeanConfig.SmsMessageQueue})
@Component
@Slf4j
public class SendSmsNotification {

    @RabbitHandler
    public void send(SmsMessage smsMessage) {
        // TODO 真实发送短信
    }
}
