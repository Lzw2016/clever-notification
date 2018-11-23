package org.clever.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.ServiceSysQueryReq;
import org.clever.notification.dto.request.ServiceSysUpdateReq;
import org.clever.notification.entity.ServiceSys;
import org.clever.notification.mapper.ServiceSysMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 19:40 <br/>
 */
@Transactional(readOnly = true)
@Service
@Slf4j
public class ServiceSysService {

    // TODO 全局使用接入系统配置

    @Autowired
    private ServiceSysMapper serviceSysMapper;

    public IPage<ServiceSys> findByPage(ServiceSysQueryReq queryReq) {
        Page<ServiceSys> page = new Page<>(queryReq.getPageNo(), queryReq.getPageSize());
        page.setRecords(serviceSysMapper.findByPage(queryReq, page));
        return page;
    }

    @Transactional
    public ServiceSys addServiceSys(ServiceSys serviceSys) {
        ServiceSys exists = serviceSysMapper.getBySysName(serviceSys.getSysName());
        if (exists != null) {
            throw new BusinessException("接入系统已经存在");
        }
        serviceSysMapper.insert(serviceSys);
        return serviceSysMapper.selectById(serviceSys.getId());
    }

    @Transactional
    public ServiceSys updateServiceSys(Long id, ServiceSysUpdateReq updateReq) {
        ServiceSys oldServiceSys = serviceSysMapper.selectById(id);
        if (oldServiceSys == null) {
            throw new BusinessException("更新数据不存在");
        }
        // 更新数据
        ServiceSys newServiceSys = BeanMapper.mapper(updateReq, ServiceSys.class);
        newServiceSys.setId(oldServiceSys.getId());
        serviceSysMapper.updateById(newServiceSys);
        return serviceSysMapper.selectById(newServiceSys.getId());
    }

    @Transactional
    public ServiceSys delServiceSys(Long id) {
        ServiceSys serviceSys = serviceSysMapper.selectById(id);
        if (serviceSys == null) {
            throw new BusinessException("删除数据不存在");
        }
        serviceSysMapper.deleteById(serviceSys.getId());
        return serviceSys;
    }
}
