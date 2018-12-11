package org.clever.notification.send.rabbit.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.model.SmsMessage;
import org.clever.notification.send.IDistinctSendId;
import org.clever.notification.send.IExcludeBlackList;
import org.clever.notification.send.IFrequencyLimit;
import org.clever.notification.send.rabbit.RabbitConfig;
import org.clever.notification.send.rabbit.RetryConsumer;
import org.clever.notification.service.SendSmsService;
import org.clever.notification.utils.AsyncNotice;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SendSmsService sendSmsService;
    @Autowired
    private IExcludeBlackList excludeBlackList;
    @Autowired
    private IFrequencyLimit frequencyLimit;
    @Autowired
    private IDistinctSendId distinctSendId;

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
    protected Action handle(SmsMessage smsMessage) {
        log.info("### 处理发送短信 {} 通知地址 -> {}", smsMessage.getSendId(), smsMessage.getAsyncCallBack());
        try {
            // 去重 Message SendId
            if (distinctSendId.existsSendId(smsMessage.getSendId())) {
                return Action.REJECT;
            }
            // 黑名单限制
            smsMessage = excludeBlackList.removeBlackList(smsMessage);
            // 发送频率限制
            smsMessage = frequencyLimit.removeFrequencyLimit(smsMessage);
            if (sendSmsService.sendSms(smsMessage)) {
                // 异步通知成功
                AsyncNotice.successNotice(smsMessage);
                log.info("### 处理发送短信 [成功] {}", smsMessage.getSendId());
            } else {
                // 异步通知失败
                AsyncNotice.failNotice(smsMessage);
                log.info("### 处理发送短信 [失败] {}", smsMessage.getSendId());
            }
        } catch (Throwable e) {
            log.error("### 处理发送短信[错误] {}", smsMessage.getSendId());
            // 异步通知失败
            AsyncNotice.failNotice(smsMessage);
            // TODO 记录或通知当前错误
            // 抛出异常 Nack
            throw e;
        }
        return Action.ACCEPT;
    }
}
