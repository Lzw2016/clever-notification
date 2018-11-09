package org.clever.notification.controller;

import io.swagger.annotations.Api;
import org.clever.notification.service.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-09 23:09 <br/>
 */
@Api(description = "管理消息模版")
@RestController
@RequestMapping("/api/manage")
public class ManageByMessageTemplateController {

    @Autowired
    private MessageTemplateService messageTemplateService;
}
