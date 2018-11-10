package org.clever.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.clever.notification.dto.request.ReceiverBlackListQueryReq;
import org.clever.notification.entity.ReceiverBlackList;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-30 19:35 <br/>
 */
public interface ReceiverBlackListMapper extends BaseMapper<ReceiverBlackList> {

    int updateEnabledByExpiredTime();

    List<ReceiverBlackList> findAllEnabled();

    List<ReceiverBlackList> findByPage(@Param("query") ReceiverBlackListQueryReq query, IPage page);

    int exists(@Param("sysName") String sysName, @Param("messageType") Integer messageType, @Param("account") String account);
}
