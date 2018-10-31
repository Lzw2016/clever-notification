package org.clever.notification.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.response.BaseResponse;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 14:14 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SendEmailRes extends BaseResponse {

    @ApiModelProperty("消息发送ID (全局唯一)")
    private Long sendId;

    @ApiModelProperty("邮件内容")
    private String content;

    @ApiModelProperty("设置收件人")
    private List<String> to;

    @ApiModelProperty("设置邮件主题")
    private String subject;

    @ApiModelProperty("设置抄送人")
    private List<String> cc;

    @ApiModelProperty("设置密送人")
    private List<String> bcc;
}
