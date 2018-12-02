package org.clever.notification.send.rabbit.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.model.SmsMessage;
import org.clever.notification.send.rabbit.RabbitConfig;
import org.clever.notification.send.rabbit.RetryConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 21:53 <br/>
 */
@RabbitListener(queues = {"notification-sms"})
@Component
@Slf4j
public class SendSmsNotification extends RetryConsumer<SmsMessage> {

    public SendSmsNotification(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    @RabbitHandler
    public void onMessage(
            @Payload SmsMessage smsMessage,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(name = RetryCount, defaultValue = "0") int retryCount,
            Channel channel) {
        try {
            super.onMessage(smsMessage, RabbitConfig.Sms, deliveryTag, retryCount, channel);
        } catch (IOException e) {
            log.info("### 消息处理失败");
        }
    }

    @Override
    protected Action handle(SmsMessage message) {
        // TODO 真实发送短信
        return Action.ACCEPT;
    }
}
