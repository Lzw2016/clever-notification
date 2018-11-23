package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;
import org.clever.common.validation.ValidIntegerStatus;

import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 14:39 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FrequencyLimitQueryReq extends QueryByPage {

    @ApiModelProperty("系统名称(全局使用“root”名称)")
    private String sysName;

    @ApiModelProperty("消息类型，1：邮件；2：短信；...(消息类型为空表示对系统的限制)")
    private Integer messageType;

    @ApiModelProperty("限速帐号(帐号为空表示对消息类型的限制)")
    private String account;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;

    @ApiModelProperty("限速配置过期时间(到期自动禁用) - 开始")
    private Date expiredTimeStart;

    @ApiModelProperty("限速配置过期时间(到期自动禁用) - 结束")
    private Date expiredTimeEnd;
}
