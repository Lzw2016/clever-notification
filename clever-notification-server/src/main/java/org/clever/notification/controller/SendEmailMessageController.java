package org.clever.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.SendEmailByContentReq;
import org.clever.notification.dto.request.SendEmailByTemplateReq;
import org.clever.notification.dto.response.SendEmailRes;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.SendEmailMessage;
import org.clever.notification.service.MessageTemplateService;
import org.clever.notification.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 12:40 <br/>
 */
@Api(description = "发送邮件消息")
@RestController
@RequestMapping("/api")
public class SendEmailMessageController {

    @Autowired
    private SendEmailMessage asyncSendEmailMessage;
    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private SendEmailService sendEmailService;

    @ApiOperation("发送邮件(使用模版)")
    @PostMapping("/send/email/template")
    public SendEmailRes sendEmail(@RequestBody @Validated SendEmailByTemplateReq req) {
        EmailMessage emailMessage = BeanMapper.mapper(req, EmailMessage.class);
        emailMessage.valid();
        // 校验消息模版是否存在
        if (!messageTemplateService.templateExists(emailMessage.getTemplateName())) {
            throw new BusinessException("消息模板不存在");
        }
        // 验证发送邮件帐号是否已经配置
        if (!sendEmailService.sendMailUtilsExists(emailMessage.getSysName())) {
            throw new BusinessException("验证发送邮件帐号未配置");
        }
        if (req.isAsync()) {
            // 异步
            emailMessage = asyncSendEmailMessage.asyncSend(emailMessage);
        } else {
            // 同步
            emailMessage = asyncSendEmailMessage.send(emailMessage);
        }
        return BeanMapper.mapper(emailMessage, SendEmailRes.class);
    }

    @ApiOperation("发送邮件(使用内容)")
    @PostMapping("/send/email/content")
    public SendEmailRes sendEmail(@RequestBody @Validated SendEmailByContentReq req) {
        EmailMessage emailMessage = BeanMapper.mapper(req, EmailMessage.class);
        emailMessage.valid();
        // 验证发送邮件帐号是否已经配置
        if (!sendEmailService.sendMailUtilsExists(emailMessage.getSysName())) {
            throw new BusinessException("验证发送邮件帐号未配置");
        }
        if (req.isAsync()) {
            // 异步
            emailMessage = asyncSendEmailMessage.asyncSend(emailMessage);
        } else {
            // 同步
            emailMessage = asyncSendEmailMessage.send(emailMessage);
        }
        return BeanMapper.mapper(emailMessage, SendEmailRes.class);
    }
}
