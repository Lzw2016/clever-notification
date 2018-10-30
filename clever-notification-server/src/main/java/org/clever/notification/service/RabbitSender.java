//package org.clever.notification.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.support.CorrelationData;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * 作者： lzw<br/>
// * 创建时间：2018-10-29 10:33 <br/>
// */
//@Component
//@Slf4j
//public class RabbitSender {
//
//    //自动注入RabbitTemplate模板类
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    //回调函数: confirm确认
//    private final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
//        log.info("------------------------------------------------------------- confirm确认");
//        log.info("correlationData: {}", correlationData);
//        log.info("ack: {}", ack);
//        log.info("cause: {}", cause);
//        log.info("-------------------------------------------------------------");
//    };
//
//    //回调函数: return返回
//    private final RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> {
//        log.info("------------------------------------------------------------- return返回");
//        log.info("message: {}", message);
//        log.info("replyCode: {}", replyCode);
//        log.info("replyText: {}", replyText);
//        log.info("exchange: {}", exchange);
//        log.info("routingKey: {}", routingKey);
//        log.info("-------------------------------------------------------------");
//    };
//
//    //发送消息方法调用: 构建Message消息
//    public void send(Object message, Map<String, Object> properties) {
//        MessageHeaders mhs = new MessageHeaders(properties);
//        Message msg = MessageBuilder.createMessage(message, mhs);
//        rabbitTemplate.setConfirmCallback(confirmCallback);
//        rabbitTemplate.setReturnCallback(returnCallback);
//        //sendId + 时间戳 全局唯一
//        CorrelationData correlationData = new CorrelationData("1234567890");
//        rabbitTemplate.convertAndSend("exchange-1", "springboot.abc", msg, correlationData);
//        log.info("RabbitTemplate -> {}", rabbitTemplate);
//    }
//}
