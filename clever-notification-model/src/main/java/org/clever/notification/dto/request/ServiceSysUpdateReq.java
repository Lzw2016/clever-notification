package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.ValidIntegerStatus;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 20:03 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceSysUpdateReq extends BaseRequest {

    @ApiModelProperty("是否启用黑名单，0：禁用；1：启用")
    @NotNull
    @ValidIntegerStatus(value = {0, 1})
    private Integer enableBlackList;

    @ApiModelProperty("黑名单最大数量(小于等于0表示不限制)")
    @NotNull
    private Integer blackListMaxCount;

    @ApiModelProperty("是否启用发送评率限制，0：禁用；1：启用")
    @NotNull
    @ValidIntegerStatus(value = {0, 1})
    private Integer enableFrequencyLimit;

    @ApiModelProperty("限制消息发送频率配置的最大数量(小于等于0表示不限制)")
    @NotNull
    private Integer frequencyLimitMaxCount;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @NotNull
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;

    @ApiModelProperty("说明")
    @Length(max = 511)
    private String description;
}
