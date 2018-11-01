package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 10:18 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysBindEmailAddReq extends BaseRequest {

    @ApiModelProperty("系统名称(全局使用“root”名称)")
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9\\u4e00-\\u9fa5()\\[\\]{}_-]{3,127}")
    private String sysName;

    @ApiModelProperty("发送人的邮箱帐号")
    @NotBlank
    @Email
    private String account;

    @ApiModelProperty("发送人的邮箱密码(加密)")
    @NotBlank
    private String password;

    @ApiModelProperty("发送人的名称")
    @NotBlank
    private String fromName;

    @ApiModelProperty("SMTP服务器地址")
    private String smtpHost;

    @ApiModelProperty("POP3服务器地址")
    private String pop3Host;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @NotNull
    @Range(min = 0, max = 1)
    private Integer enabled;
}
