package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;
import org.clever.common.validation.ValidIntegerStatus;

import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 13:55 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageSendLogQueryReq extends QueryByPage {

    @ApiModelProperty("消息发送ID")
    private Long sendId;

    @ApiModelProperty("系统名称")
    private String sysName;

    @ApiModelProperty("消息类型，1：邮件；2：短信；...")
    private Integer messageType;

    @ApiModelProperty("消息模版名称")
    private String templateName;

    @ApiModelProperty("发送状态，1：发送中；2：发送失败；3：发送成功")
    @ValidIntegerStatus(value = {1, 2, 3})
    private Integer sendState;

    @ApiModelProperty("发送时间 - 开始")
    private Date sendTimeStart;

    @ApiModelProperty("发送时间 - 结束")
    private Date sendTimeEnd;

    @ApiModelProperty("发送消息耗时(毫秒) - 最小")
    private Long useTimeMin;

    @ApiModelProperty("发送消息耗时(毫秒) - 最大")
    private Long useTimeMax;
}
