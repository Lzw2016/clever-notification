package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;
import org.clever.common.validation.ValidIntegerStatus;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 19:43 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceSysQueryReq extends QueryByPage {

    @ApiModelProperty("系统(或服务)名称")
    private String sysName;

    @ApiModelProperty("是否启用黑名单，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enableBlackList;

    @ApiModelProperty("是否启用发送评率限制，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enableFrequencyLimit;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;
}
