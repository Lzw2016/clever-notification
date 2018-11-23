package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;
import org.clever.common.validation.ValidIntegerStatus;

import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 16:03 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReceiverBlackListQueryReq extends QueryByPage {

    @ApiModelProperty("系统名称(全局使用“root”名称)")
    private String sysName;

    @ApiModelProperty("消息类型，1：邮件；2：短信；...")
    private Integer messageType;

    @ApiModelProperty("黑名单帐号")
    private String account;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;

    @ApiModelProperty("黑名单帐号过期时间(到期自动禁用) - 开始")
    private Date expiredTimeStart;

    @ApiModelProperty("黑名单帐号过期时间(到期自动禁用) - 结束")
    private Date expiredTimeEnd;
}
