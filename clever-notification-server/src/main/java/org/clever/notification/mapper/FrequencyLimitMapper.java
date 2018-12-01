package org.clever.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.clever.notification.dto.request.FrequencyLimitQueryReq;
import org.clever.notification.entity.FrequencyLimit;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 22:33 <br/>
 */
@Repository
@Mapper
public interface FrequencyLimitMapper extends BaseMapper<FrequencyLimit> {

    int updateEnabledByExpiredTime();

    List<FrequencyLimit> findAllEnabled();

    List<FrequencyLimit> findByPage(@Param("query") FrequencyLimitQueryReq query, IPage page);

    int exists(@Param("sysName") String sysName, @Param("messageType") Integer messageType, @Param("account") String account);
}
