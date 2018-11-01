package org.clever.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.SysBindEmailAddReq;
import org.clever.notification.dto.request.SysBindEmailQueryReq;
import org.clever.notification.entity.SysBindEmail;
import org.clever.notification.service.ManageBySysBindEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 10:14 <br/>
 */
@Api(description = "管理系统邮件发送者帐号")
@RestController
@RequestMapping("/api/manage")
public class ManageBySysBindEmailController {

    @Autowired
    private ManageBySysBindEmailService manageBySysBindEmailService;

    @ApiOperation("分页查询邮件发送者帐号")
    @GetMapping("/sys_bind_email")
    public IPage<SysBindEmail> findByPage(SysBindEmailQueryReq req) {
        return manageBySysBindEmailService.findByPage(req);
    }

    @ApiOperation("新增邮件发送者帐号")
    @PostMapping("/sys_bind_email")
    public SysBindEmail addSysBindEmail(@RequestBody @Validated SysBindEmailAddReq req) {
        SysBindEmail sysBindEmail = manageBySysBindEmailService.addSysBindEmail(BeanMapper.mapper(req, SysBindEmail.class));
        sysBindEmail.setPassword(null);
        return sysBindEmail;
    }
}
