package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;
import org.hibernate.validator.constraints.Range;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 21:18 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysBindEmailQueryReq extends QueryByPage {

    @ApiModelProperty("系统名称(全局使用“root”名称)")
    private String sysName;

    @ApiModelProperty("发送人的邮箱帐号(模糊匹配)")
    private String account;

    @ApiModelProperty("发送人的名称(模糊匹配)")
    private String fromName;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @Range(min = 0, max = 1)
    private Integer enabled;
}
