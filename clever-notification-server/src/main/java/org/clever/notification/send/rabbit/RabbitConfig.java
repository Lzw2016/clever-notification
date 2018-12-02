package org.clever.notification.send.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 14:36 <br/>
 */

@SuppressWarnings("WeakerAccess")
@Component
@Slf4j
public class RabbitConfig {

    /**
     * DLX
     */
    private static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";

    /**
     * DLK
     */
    private static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    /**
     * 重试标识名称
     */
    private static final String Retry_Name = "retry";

    /**
     * 交换器名称
     */
    public static final String ExchangeName = "notification";

    /**
     * 消息类型 - email
     */
    public static final String Email = "email";

    /**
     * 消息类型 - sms
     */
    public static final String Sms = "sms";

    @Autowired
    private AmqpAdmin amqpAdmin;

    private void initMessageQueue(String messageType) {
        log.info("### [开始]初始化消息队列 -> [{}]", messageType);
        // 申明交换器
        Exchange exchange = ExchangeBuilder
                .topicExchange(ExchangeName)
                .durable(true)
                .build();
        amqpAdmin.declareExchange(exchange);
        // 申明队列
        Queue queue = QueueBuilder
                .durable(getQueueName(messageType))
                .build();
        amqpAdmin.declareQueue(queue);
        // 申明重试队列
        Queue retryQueue = QueueBuilder
                .durable(getRetryQueueName(messageType))
                .withArgument(DEAD_LETTER_QUEUE_KEY, ExchangeName)
                .withArgument(DEAD_LETTER_ROUTING_KEY, getDLK(messageType))
                .build();
        amqpAdmin.declareQueue(retryQueue);
        // 队列绑定交换器
        Binding binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(getRoutingKey(messageType))
                .noargs();
        amqpAdmin.declareBinding(binding);
        // 重试队列绑定交换器
        Binding retryBinding = BindingBuilder
                .bind(retryQueue)
                .to(exchange)
                .with(getRetryRoutingKey(messageType))
                .noargs();
        amqpAdmin.declareBinding(retryBinding);
        log.info("### [成功]初始化消息队列 -> [{}]", messageType);
    }

    @PostConstruct
    protected void init() {
        initMessageQueue(Email);
        initMessageQueue(Sms);
    }

    /**
     * 获取队列名称
     *
     * @param messageType 消息类型
     */
    private static String getQueueName(String messageType) {
        return String.format("%s-%s", ExchangeName, messageType);
    }

    /**
     * 获取重试队列名称
     *
     * @param messageType 消息类型
     */
    private static String getRetryQueueName(String messageType) {
        return String.format("%s-%s-%s", ExchangeName, messageType, Retry_Name);
    }

    /**
     * 获取 DLK
     *
     * @param messageType 消息类型
     */
    private static String getDLK(String messageType) {
        return String.format("%s.%s.%s", ExchangeName, messageType, Retry_Name);
    }

    /**
     * 获取队列路由Key
     *
     * @param messageType 消息类型
     */
    private static String getRoutingKey(String messageType) {
        return String.format("%s.%s.#", ExchangeName, messageType);
    }

    /**
     * 获取队列路由Key
     *
     * @param messageType 消息类型
     * @param system      系统名
     */
    public static String getRoutingKey(String messageType, String system) {
        return String.format("%s.%s.%s", ExchangeName, messageType, system);
    }

    /**
     * 获取重试队列路由Key
     *
     * @param messageType 消息类型
     */
    private static String getRetryRoutingKey(String messageType) {
        return String.format("%s.%s.%s.#", ExchangeName, Retry_Name, messageType);
    }

    /**
     * 获取重试队列路由Key
     *
     * @param messageType 消息类型
     * @param system      系统名
     */
    public static String getRetryRoutingKey(String messageType, String system) {
        return String.format("%s.%s.%s.%s", ExchangeName, Retry_Name, messageType, system);
    }
}
