package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.PatternConstant;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.StringNotBlank;
import org.clever.common.validation.ValidIntegerStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 14:54 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FrequencyLimitAddReq extends BaseRequest {

    @ApiModelProperty("系统名称(全局使用“root”名称)")
    @NotBlank
    @Pattern(regexp = PatternConstant.Name_Pattern + "{1,127}")
    private String sysName;

    @ApiModelProperty("消息类型，1：邮件；2：短信；...(消息类型为空表示对系统的限制)")
    private Integer messageType;

    @ApiModelProperty("限速帐号(帐号为空表示对消息类型的限制)")
    @StringNotBlank
    private String account;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;

    @ApiModelProperty("限速配置过期时间(到期自动禁用)")
    @Future
    private Date expiredTime;

    @ApiModelProperty("一分钟内的发送次数(小于等于0表示不限制)")
    private Integer minutesCount = -1;

    @ApiModelProperty("一小时内的发送次数(小于等于0表示不限制)")
    private Integer hoursCount = -1;

    @ApiModelProperty("一天内的发送次数(小于等于0表示不限制)")
    private Integer daysCount = -1;

    @ApiModelProperty("一周内的发送次数(小于等于0表示不限制)")
    private Integer weeksCount = -1;

    @ApiModelProperty("一月内的发送次数(小于等于0表示不限制)")
    private Integer monthsCount = -1;
}
