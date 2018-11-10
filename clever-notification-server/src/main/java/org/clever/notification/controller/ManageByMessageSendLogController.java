package org.clever.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.notification.dto.request.MessageSendLogQueryReq;
import org.clever.notification.entity.MessageSendLog;
import org.clever.notification.service.MessageSendLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @ApiOperation("分页查询消息发送日志")
    @GetMapping("/message_send_log")
    public IPage<MessageSendLog> findByPage(MessageSendLogQueryReq queryReq) {
        return messageSendLogService.findByPage(queryReq);
    }
}
