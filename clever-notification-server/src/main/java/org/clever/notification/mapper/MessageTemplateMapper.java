package org.clever.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.clever.notification.dto.request.MessageTemplateQueryReq;
import org.clever.notification.entity.MessageTemplate;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-30 19:34 <br/>
 */
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {

    List<MessageTemplate> findAllEnabled();

    List<MessageTemplate> findByPage(@Param("query") MessageTemplateQueryReq query, IPage page);

    int exists(@Param("name") String name);

    MessageTemplate getByName(@Param("name") String name);
}
