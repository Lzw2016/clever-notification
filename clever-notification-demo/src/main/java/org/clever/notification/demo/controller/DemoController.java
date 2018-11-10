package org.clever.notification.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.notification.client.SendEmailMessageClient;
import org.clever.notification.dto.request.SendEmailByContentReq;
import org.clever.notification.dto.response.SendEmailRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 20:42 <br/>
 */
@Api(description = "消息发送示例")
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private SendEmailMessageClient sendEmailMessageClient;

    @ApiOperation("发送邮件")
    @GetMapping("/send_email")
    public SendEmailRes sendEmail() {
        SendEmailByContentReq sendEmailByContentReq = new SendEmailByContentReq();
        sendEmailByContentReq.setAsync(true);
        sendEmailByContentReq.setAsyncCallBack("https://www.baidu.com/");
        sendEmailByContentReq.setSysName("Test01");
        sendEmailByContentReq.setContent("活动地址");
        sendEmailByContentReq.setSubject("双11活动");
        sendEmailByContentReq.setTo(Collections.singletonList("1183409807@qq.com"));
        return sendEmailMessageClient.sendEmail(sendEmailByContentReq);
    }
}
