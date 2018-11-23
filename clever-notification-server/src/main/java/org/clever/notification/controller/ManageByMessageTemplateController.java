package org.clever.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.MessageTemplateAddReq;
import org.clever.notification.dto.request.MessageTemplateQueryReq;
import org.clever.notification.dto.request.MessageTemplateUpdateReq;
import org.clever.notification.entity.MessageTemplate;
import org.clever.notification.service.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("分页查询消息模版")
    @GetMapping("/message_template")
    public IPage<MessageTemplate> findByPage(MessageTemplateQueryReq query) {
        return messageTemplateService.findByPage(query);
    }

    @ApiOperation("新增消息模版")
    @PostMapping("/message_template")
    public MessageTemplate addMessageTemplate(@RequestBody @Validated MessageTemplateAddReq addReq) {
        MessageTemplate messageTemplate = BeanMapper.mapper(addReq, MessageTemplate.class);
        return messageTemplateService.addMessageTemplate(messageTemplate);
    }

    @ApiOperation("更新消息模版")
    @PutMapping("/message_template/{id}")
    public MessageTemplate updateMessageTemplate(@PathVariable("id") Long id, @RequestBody @Validated MessageTemplateUpdateReq updateReq) {
        return messageTemplateService.updateMessageTemplate(id, updateReq);
    }

    @ApiOperation("删除消息模版")
    @DeleteMapping("/message_template/{id}")
    public MessageTemplate delMessageTemplate(@PathVariable("id") Long id) {
        return messageTemplateService.delMessageTemplate(id);
    }
}
