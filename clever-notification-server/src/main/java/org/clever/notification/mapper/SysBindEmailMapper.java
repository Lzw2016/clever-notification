package org.clever.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.clever.notification.dto.request.SysBindEmailQueryReq;
import org.clever.notification.entity.SysBindEmail;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-30 19:35 <br/>
 */
public interface SysBindEmailMapper extends BaseMapper<SysBindEmail> {

    List<SysBindEmail> getAllEnabled();

    int existsAccount(@Param("account") String account);

    SysBindEmail getByAccount(@Param("account") String account);

    List<SysBindEmail> findByPage(@Param("query") SysBindEmailQueryReq query, IPage page);
}
