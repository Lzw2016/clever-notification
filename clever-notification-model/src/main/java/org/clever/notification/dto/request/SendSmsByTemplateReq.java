package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.clever.notification.dto.PatternConstant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 21:37 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SendSmsByTemplateReq extends BaseRequest {

    @ApiModelProperty("是否是异步发送(默认异步)，true:异步；false:同步")
    private boolean async = true;

    @ApiModelProperty("异步发送时的回调接口")
    @Pattern(regexp = PatternConstant.Url_Pattern)
    @Length(max = 1024)
    private String asyncCallBack;

    @ApiModelProperty("消息所属系统名称")
    @NotBlank
    private String sysName;

    @ApiModelProperty("消息模板名称")
    @NotBlank
    private String templateName;

    @ApiModelProperty("模板需要参数")
    private Map<String, Object> params;

    @ApiModelProperty("接收手机号，不能为空")
    @NotBlank
    @Pattern(regexp = PatternConstant.Telephone_Pattern)
    private String to;

    public SendSmsByTemplateReq() {

    }

    /**
     * @param sysName      消息所属系统名称
     * @param templateName 消息模板名称
     * @param to           接收手机号
     */
    public SendSmsByTemplateReq(String sysName, String templateName, String to) {
        this.sysName = sysName;
        this.templateName = templateName;
        this.to = to;
    }

    /**
     * @param sysName      消息所属系统名称
     * @param templateName 消息模板名称
     * @param params       模板需要参数
     * @param to           接收手机号
     */
    public SendSmsByTemplateReq(String sysName, String templateName, Map<String, Object> params, String to) {
        this.sysName = sysName;
        this.templateName = templateName;
        this.params = params;
        this.to = to;
    }
}
