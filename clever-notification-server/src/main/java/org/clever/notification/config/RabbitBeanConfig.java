package org.clever.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 14:36 <br/>
 */
@SuppressWarnings("WeakerAccess")
@Configuration
@Slf4j
public class RabbitBeanConfig {

    private static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";
    private static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    //--------------------------------------------------------------------------------------------------------------------------------------
    //     Exchange
    // -------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 消息交换器
     */
    public static final String MessageExchange = "message-notification";

    /**
     * 消息交换器 - 死信交换器
     */
    public static final String MessageExchangeDead = "message-notification-dead";

    //--------------------------------------------------------------------------------------------------------------------------------------
    //     Queue
    // -------------------------------------------------------------------------------------------------------------------------------------

    /**
     * email 消息通知队列
     */
    public static final String EmailMessageQueue = "email-notification";

    /**
     * sms 消息通知队列
     */
    public static final String SmsMessageQueue = "sms-notification";

    /**
     * email 消息通知 - 死信队列
     */
    public static final String EmailMessageQueueDead = "email-notification-dead";

    /**
     * sms 消息通知 - 死信队列
     */
    public static final String SmsMessageQueueDead = "sms-notification-dead";

    //--------------------------------------------------------------------------------------------------------------------------------------
    //     Routing Key
    // -------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 邮件路由key - 前缀
     */
    public static final String EmailRoutingKey = "notification.email";

    /**
     * 短信路由key - 前缀
     */
    public static final String SmsRoutingKey = "notification.sms";

    /**
     * 邮件路由key - 死信路由key
     */
    public static final String EmailDeadRoutingKey = "notification.dead.email";

    /**
     * 短信路由key - 死信路由key
     */
    public static final String SmsDeadRoutingKey = "notification.dead.sms";

    //--------------------------------------------------------------------------------------------------------------------------------------
    //     配置队列、交换器
    // -------------------------------------------------------------------------------------------------------------------------------------

    @Bean("messageTopicExchange")
    public Exchange messageTopicExchange() {
        return ExchangeBuilder.topicExchange(MessageExchange)
                .durable(true)
                .build();
    }

    @Bean("emailQueue")
    public Queue emailQueue() {
        return QueueBuilder.durable(EmailMessageQueue)
                .withArgument(DEAD_LETTER_QUEUE_KEY, MessageExchangeDead)
                .withArgument(DEAD_LETTER_ROUTING_KEY, EmailDeadRoutingKey)
                .build();
    }

    @Bean("smsQueue")
    public Queue smsQueue() {
        return QueueBuilder.durable(SmsMessageQueue)
                .withArgument(DEAD_LETTER_QUEUE_KEY, MessageExchangeDead)
                .withArgument(DEAD_LETTER_ROUTING_KEY, SmsDeadRoutingKey)
                .build();
    }

    @Bean
    public Binding emailQueueBinding(@Qualifier("emailQueue") Queue emailQueue, @Qualifier("messageTopicExchange") Exchange messageTopicExchange) {
        return BindingBuilder.bind(emailQueue).to(messageTopicExchange).with(String.format("%s.#", EmailRoutingKey)).noargs();
    }

    @Bean
    public Binding smsQueueBinding(@Qualifier("smsQueue") Queue smsQueue, @Qualifier("messageTopicExchange") Exchange messageTopicExchange) {
        return BindingBuilder.bind(smsQueue).to(messageTopicExchange).with(String.format("%s.#", SmsRoutingKey)).noargs();
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    //     死信队列、死信交换器
    // -------------------------------------------------------------------------------------------------------------------------------------

    @Bean("deadExchange")
    public Exchange deadExchange() {
        return ExchangeBuilder.directExchange(MessageExchangeDead)
                .durable(true)
                .build();
    }

    @Bean("emailQueueDead")
    public Queue emailQueueDead() {
        return QueueBuilder.durable(EmailMessageQueueDead)
                .build();
    }

    @Bean("smsQueueDead")
    public Queue smsQueueDead() {
        return QueueBuilder.durable(SmsMessageQueueDead)
                .build();
    }

    @Bean("emailQueueDeadBinding")
    public Binding emailQueueDeadBinding(@Qualifier("emailQueueDead") Queue emailQueueDead, @Qualifier("deadExchange") Exchange deadExchange) {
        return BindingBuilder.bind(emailQueueDead).to(deadExchange).with(EmailDeadRoutingKey).noargs();
    }

    @Bean("smsQueueDeadBinding")
    public Binding smsQueueDeadBinding(@Qualifier("smsQueueDead") Queue smsQueueDead, @Qualifier("deadExchange") Exchange deadExchange) {
        return BindingBuilder.bind(smsQueueDead).to(deadExchange).with(SmsDeadRoutingKey).noargs();
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    //     初始化队列
    // -------------------------------------------------------------------------------------------------------------------------------------

//    @Autowired
//    private AmqpAdmin amqpAdmin;
//    @Autowired
//    private List<Queue> queues;
//    @Autowired
//    private List<Exchange> exchanges;
//    @Autowired
//    private List<Binding> bindings;

//    @PostConstruct
//    public void init() {
//        log.info("################ {} {} {}", queues.size(), exchanges.size(), bindings.size());
//        for (Exchange exchange : exchanges) {
//            log.info("### exchange={}", exchange.getName());
//            amqpAdmin.declareExchange(exchange);
//        }
//        for (Queue queue : queues) {
//            log.info("### queue={}", queue.getName());
//            amqpAdmin.declareQueue(queue);
//        }
//        for (Binding binding : bindings) {
//            log.info("### binding={} RoutingKey={}", binding.getExchange(), binding.getRoutingKey());
//            amqpAdmin.declareBinding(binding);
//        }
//        log.info("##################### 完成");
//    }
}
