//package org.clever.notification.service;
//
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.*;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.stereotype.Component;
//
///**
// * 作者： lzw<br/>
// * 创建时间：2018-10-29 10:41 <br/>
// */
//@Component
//@Slf4j
//public class RabbitReceiver {
//
//    @RabbitListener(
//            bindings = {
//                    @QueueBinding(
//                            key = {"springboot.*"},
//                            value = @Queue(value = "queue-1", durable = "true"),
//                            exchange = @Exchange(value = "exchange-1", durable = "true", type = "topic", ignoreDeclarationExceptions = "true")
//                    )
//            }
//    )
//    @RabbitHandler
//    public void onMessage(Message message, Channel channel) throws Exception {
//        Thread.sleep(1000 * 5);
//        log.info("------------------------------------------------------------------ onMessage");
//        log.info("message: {}", message);
//        log.info("------------------------------------------------------------------");
//        long deliveryTag = (long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
//        //手工ACK
//        channel.basicAck(deliveryTag, false);
//    }
//}
