package org.clever.notification.controller;

import io.swagger.annotations.Api;
import org.clever.notification.service.MessageSendLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-09 23:13 <br/>
 */
@Api(description = "发送日志管理")
@RestController
@RequestMapping("/api/manage")
public class ManageByMessageSendLogController {

    @Autowired
    private MessageSendLogService messageSendLogService;
}
