package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.PatternConstant;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.ValidIntegerStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 17:33 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReceiverBlackListAddReq extends BaseRequest {

    @ApiModelProperty("系统名称(全局使用“root”名称)")
    @NotBlank
    @Pattern(regexp = PatternConstant.Name_Pattern + "{3,127}")
    private String sysName;

    @ApiModelProperty("消息类型，1：邮件；2：短信；...")
    @NotNull
    private Integer messageType;

    @ApiModelProperty("黑名单帐号")
    @NotBlank
    private String account;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;

    @ApiModelProperty("黑名单帐号过期时间(到期自动禁用)")
    @Future
    private Date expiredTime;
}
