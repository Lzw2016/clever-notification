package org.clever.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.SendSmsByTemplateReq;
import org.clever.notification.dto.response.SendSmsRes;
import org.clever.notification.model.SmsMessage;
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

    @ApiOperation("发送短信(使用模版)")
    @PostMapping("/send/sms/template")
    public SendSmsRes sendSms(@RequestBody @Validated SendSmsByTemplateReq req) {
        SmsMessage smsMessage = BeanMapper.mapper(req, SmsMessage.class);
        smsMessage.valid();
//        // 校验消息模版是否存在
//        if (!messageTemplateService.templateExists(emailMessage.getTemplateName())) {
//            throw new BusinessException("消息模板不存在");
//        }
//        // 验证发送邮件帐号是否已经配置
//        if (!sendEmailService.sendMailUtilsExists(emailMessage.getSysName())) {
//            throw new BusinessException("验证发送邮件帐号未配置");
//        }
//        if (req.isAsync()) {
//            // 异步
//            emailMessage = asyncSendEmailMessage.asyncSend(emailMessage);
//        } else {
//            // 同步
//            emailMessage = asyncSendEmailMessage.send(emailMessage);
//        }
        return BeanMapper.mapper(smsMessage, SendSmsRes.class);
    }

}
