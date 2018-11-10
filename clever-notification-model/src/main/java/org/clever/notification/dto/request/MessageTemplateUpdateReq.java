package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.PatternConstant;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.StringNotBlank;
import org.clever.common.validation.ValidIntegerStatus;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 13:05 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageTemplateUpdateReq extends BaseRequest {

    @ApiModelProperty("模版名称")
    @StringNotBlank
    @Pattern(regexp = PatternConstant.Name_Pattern + "{3,127}")
    private String name;

    @ApiModelProperty("模版内容")
    @StringNotBlank
    private String content;

    @ApiModelProperty("模版消息示例")
    private String messageDemo;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;

    @ApiModelProperty("说明")
    @Length(max = 1203)
    private String description;
}
