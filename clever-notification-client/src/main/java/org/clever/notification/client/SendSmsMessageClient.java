package org.clever.notification.client;

import org.clever.notification.dto.request.SendSmsByTemplateReq;
import org.clever.notification.dto.response.SendSmsRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 10:04 <br/>
 */
@FeignClient(name = "clever-notification-server", path = "/api")
public interface SendSmsMessageClient {

    /**
     * 发送短信(使用模版)
     */
    @PostMapping("/send/sms/template")
    SendSmsRes sendSms(@RequestBody SendSmsByTemplateReq req);
}
