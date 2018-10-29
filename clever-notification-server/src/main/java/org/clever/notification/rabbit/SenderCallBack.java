package org.clever.notification.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 16:09 <br/>
 */
@Component
@Slf4j
public class SenderCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
//        rabbitTemplate.setBeforePublishPostProcessors();
//        rabbitTemplate.setAfterReceivePostProcessors();
    }

    /**
     * 消息发送确认
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("### 消息发送成功 -> {}", correlationData);
        } else {
            log.error("### 消息发送失败 -> [correlationData={}] [cause={}]", correlationData, cause);
        }
    }

    /**
     * 消息没有对应的队列(联合mandatory参数使用)
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("### 消息不能发送到队列 -> {}", message);
    }
}
