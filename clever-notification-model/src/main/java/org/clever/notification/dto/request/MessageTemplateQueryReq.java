package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 11:44 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageTemplateQueryReq extends QueryByPage {

    @ApiModelProperty("模版名称(模糊匹配)")
    private String name;

    @ApiModelProperty("模版内容(模糊匹配)")
    private String content;

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    private Integer enabled;
}
