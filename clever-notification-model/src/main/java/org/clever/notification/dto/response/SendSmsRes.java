package org.clever.notification.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.response.BaseResponse;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 21:45 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SendSmsRes extends BaseResponse {

    @ApiModelProperty("消息发送ID (全局唯一)")
    private Long sendId;

    @ApiModelProperty("短信内容")
    private String content;

    @ApiModelProperty("接收手机号")
    private String to;
}
