package org.clever.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.clever.notification.dto.request.ServiceSysQueryReq;
import org.clever.notification.entity.ServiceSys;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 19:37 <br/>
 */
public interface ServiceSysMapper extends BaseMapper<ServiceSys> {

    List<ServiceSys> findByPage(@Param("query") ServiceSysQueryReq query, IPage page);

    ServiceSys getBySysName(@Param("sysName") String sysName);
}
