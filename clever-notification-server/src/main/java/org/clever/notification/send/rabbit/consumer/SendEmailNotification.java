package org.clever.notification.send.rabbit.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.send.IDistinctSendId;
import org.clever.notification.send.IExcludeBlackList;
import org.clever.notification.send.IFrequencyLimit;
import org.clever.notification.send.rabbit.RabbitConfig;
import org.clever.notification.send.rabbit.RetryConsumer;
import org.clever.notification.service.SendEmailService;
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
 * 创建时间：2018-10-29 18:14 <br/>
 */
@RabbitListener(queues = {"notification-email"})
@Component
@Slf4j
public class SendEmailNotification extends RetryConsumer<EmailMessage> {

    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private IExcludeBlackList excludeBlackList;
    @Autowired
    private IFrequencyLimit frequencyLimit;
    @Autowired
    private IDistinctSendId distinctSendId;

    public SendEmailNotification(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
        // super(rabbitTemplate, RetryStrategy.DEFAULT_5_SECOND);
    }

    @RabbitHandler
    public void onMessage(
            @Payload EmailMessage emailMessage,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(name = RetryCount, defaultValue = "0") int retryCount,
            Channel channel) {
        try {
            super.onMessage(emailMessage, RabbitConfig.Email, deliveryTag, retryCount, channel);
        } catch (IOException e) {
            log.info("### 消息处理失败");
        }
    }

    @Override
    protected Action handle(EmailMessage emailMessage) {
        log.info("### 处理发送邮件 {} 通知地址 -> {}", emailMessage.getSendId(), emailMessage.getAsyncCallBack());
        try {
            // 去重 Message SendId
            if (distinctSendId.existsSendId(emailMessage.getSendId())) {
                return Action.REJECT;
            }
            // 黑名单限制
            emailMessage = excludeBlackList.removeBlackList(emailMessage);
            // 发送频率限制
            emailMessage = frequencyLimit.removeFrequencyLimit(emailMessage);
            if (sendEmailService.sendEmail(emailMessage)) {
                // 异步通知成功
                AsyncNotice.successNotice(emailMessage);
                log.info("### 处理发送邮件 [成功] {}", emailMessage.getSendId());
            } else {
                // 异步通知失败
                AsyncNotice.failNotice(emailMessage);
                log.info("### 处理发送邮件 [失败] {}", emailMessage.getSendId());
            }
        } catch (Throwable e) {
            log.error("### 处理发送邮件[错误] {}", emailMessage.getSendId());
            // 异步通知失败
            AsyncNotice.failNotice(emailMessage);
            // TODO 记录或通知当前错误
            // 抛出异常 Nack
            throw e;
        }
        return Action.ACCEPT;
    }
}
