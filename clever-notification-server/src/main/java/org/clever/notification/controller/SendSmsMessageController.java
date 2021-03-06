package org.clever.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.config.GlobalConfig;
import org.clever.notification.dto.request.SendSmsByTemplateReq;
import org.clever.notification.dto.response.SendSmsRes;
import org.clever.notification.model.SmsMessage;
import org.clever.notification.send.rabbit.producer.SendSmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 21:36 <br/>
 */
@Api(description = "发送短信消息")
@RestController
@RequestMapping("/api")
public class SendSmsMessageController {

    @Autowired
    private SendSmsMessage sendSmsMessage;

    private final GlobalConfig.AliyunSmsConfig aliyunSmsConfig;

    protected SendSmsMessageController(GlobalConfig globalConfig) {
        aliyunSmsConfig = globalConfig.getAliyunSmsConfig();
    }

    @ApiOperation("发送短信(使用模版)")
    @PostMapping("/send/sms/template")
    public SendSmsRes sendSms(@RequestBody @Validated SendSmsByTemplateReq req) {
        SmsMessage smsMessage = BeanMapper.mapper(req, SmsMessage.class);
        smsMessage.valid();
        // 校验消息模版是否存在
        if (!aliyunSmsConfig.getTemplateNames().contains(req.getTemplateName())) {
            throw new BusinessException("消息模板不存在");
        }
        if (req.isAsync()) {
            // 异步
            smsMessage = sendSmsMessage.asyncSend(smsMessage);
        } else {
            // 同步
            smsMessage = sendSmsMessage.send(smsMessage);
        }
        return BeanMapper.mapper(smsMessage, SendSmsRes.class);
    }
}
