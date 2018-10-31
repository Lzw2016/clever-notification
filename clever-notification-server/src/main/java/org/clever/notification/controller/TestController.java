package org.clever.notification.controller;

import io.swagger.annotations.Api;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.SendEmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 10:44 <br/>
 */
@Api(description = "测试")
@RestController
@RequestMapping("/test")
public class TestController {


    // TODO 批量操作
//    BatchingRabbitTemplate

//    @Autowired
//    private RabbitReceiver rabbitReceiver;
//
//    @Autowired
//    private RabbitSender rabbitSender;
//    @Autowired
//    private RabbitSender01 rabbitSender01;
//
//    @GetMapping("/01")
//    public void test01() throws Exception {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("number", "12345");
//        properties.put("send_time", DateTimeUtils.formatToString(new Date()));
//        rabbitSender.send("Hello RabbitMQ For Spring Boot!", properties);
//
//
//        rabbitSender01.send("啦啦啦啦", properties);
//    }

    @Autowired
    private SendEmailMessage sendEmailMessage;

    @GetMapping("/02")
    public EmailMessage test01() {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSysName("testSys");
        emailMessage.setContent("邮件消息");
        emailMessage.setSubject("邮件通知");
        return sendEmailMessage.send(emailMessage);
    }

    // TODO 直接发送不走MQ
}
