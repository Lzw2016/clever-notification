package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.StringNotBlank;
import org.clever.notification.dto.PatternConstant;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-09 21:06 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysBindEmailUpdateReq extends BaseRequest {

    @ApiModelProperty("系统名称(全局使用“root”名称)")
    @Pattern(regexp = PatternConstant.SysName_Pattern)
    private String sysName;

    @ApiModelProperty("发送人的邮箱帐号")
    @StringNotBlank
    @Email
    private String account;

    @ApiModelProperty("发送人的邮箱密码(加密)")
    @StringNotBlank
    private String password;

    @ApiModelProperty("发送人的名称")
    @StringNotBlank
    private String fromName;

    @ApiModelProperty("SMTP服务器地址")
    @StringNotBlank
    private String smtpHost;

    @ApiModelProperty("POP3服务器地址")
    @StringNotBlank
    private String pop3Host;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @Range(min = 0, max = 1)
    private Integer enabled;
}
