package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 21:37 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SendSmsByTemplateReq extends BaseRequest {

    @ApiModelProperty("异步发送时的回调接口")
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
    @NotNull
    @Size(min = 1, max = 500)
    private List<String> to;

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
        this.params = params;
        this.to = new ArrayList<String>() {{
            add(to);
        }};
    }

    /**
     * @param sysName      消息所属系统名称
     * @param templateName 消息模板名称
     * @param params       模板需要参数
     * @param to           接收手机号
     */
    public SendSmsByTemplateReq(String sysName, String templateName, Map<String, Object> params, String... to) {
        this.sysName = sysName;
        this.templateName = templateName;
        this.params = params;
        this.to = Arrays.asList(to);
    }
}
