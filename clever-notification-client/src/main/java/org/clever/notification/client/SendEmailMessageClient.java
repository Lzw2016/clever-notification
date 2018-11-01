package org.clever.notification.client;

import org.clever.notification.dto.request.SendEmailByContentReq;
import org.clever.notification.dto.request.SendEmailByTemplateReq;
import org.clever.notification.dto.response.SendEmailRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 10:07 <br/>
 */
@FeignClient(name = "clever-notification-server", path = "/api")
public interface SendEmailMessageClient {

    /**
     * 发送邮件(使用模版)
     */
    @PostMapping("/send/email/template")
    SendEmailRes sendEmail(@RequestBody SendEmailByTemplateReq req);

    /**
     * 发送邮件(使用内容)
     */
    @PostMapping("/send/email/content")
    SendEmailRes sendEmail(@RequestBody SendEmailByContentReq req);
}
