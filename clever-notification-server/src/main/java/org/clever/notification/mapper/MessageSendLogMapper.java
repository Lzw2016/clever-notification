package org.clever.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.clever.notification.dto.request.MessageSendLogQueryReq;
import org.clever.notification.entity.MessageSendLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-30 19:33 <br/>
 */
@Repository
@Mapper
public interface MessageSendLogMapper extends BaseMapper<MessageSendLog> {

    List<MessageSendLog> findByPage(@Param("query") MessageSendLogQueryReq query, IPage page);
}
