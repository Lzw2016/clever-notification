package org.clever.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.ServiceSysAddReq;
import org.clever.notification.dto.request.ServiceSysQueryReq;
import org.clever.notification.dto.request.ServiceSysUpdateReq;
import org.clever.notification.entity.ServiceSys;
import org.clever.notification.service.ServiceSysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 19:41 <br/>
 */
@Api(description = "管理接入系统")
@RestController
@RequestMapping("/api/manage")
public class ManageByServiceSysController {

    @Autowired
    private ServiceSysService serviceSysService;

    @ApiOperation("分页查询接入系统")
    @GetMapping("/service_sys")
    public IPage<ServiceSys> findByPage(ServiceSysQueryReq serviceSysQueryReq) {
        return serviceSysService.findByPage(serviceSysQueryReq);
    }

    @ApiOperation("新增查询接入系统")
    @PostMapping("/service_sys")
    public ServiceSys addServiceSys(@RequestBody @Validated ServiceSysAddReq addReq) {
        ServiceSys serviceSys = BeanMapper.mapper(addReq, ServiceSys.class);
        return serviceSysService.addServiceSys(serviceSys);
    }

    @ApiOperation("更新查询接入系统")
    @PutMapping("/service_sys/{id}")
    public ServiceSys updateServiceSys(@PathVariable("id") Long id, @RequestBody @Validated ServiceSysUpdateReq updateReq) {
        return serviceSysService.updateServiceSys(id, updateReq);
    }

    @ApiOperation("更新查询接入系统")
    @DeleteMapping("/service_sys/{id}")
    public ServiceSys delServiceSys(@PathVariable("id") Long id) {
        return serviceSysService.delServiceSys(id);
    }
}
