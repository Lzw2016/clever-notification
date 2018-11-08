package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.clever.notification.dto.PatternConstant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 12:45 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SendEmailByTemplateReq extends BaseRequest {

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

    @ApiModelProperty("设置邮件主题，不能为空")
    @NotBlank
    private String subject;

    @ApiModelProperty("设置收件人，不能为空")
    @NotNull
    @Size(min = 1, max = 1000)
    private List<String> to;

    @ApiModelProperty("设置抄送人，可以为空")
    @Size(max = 1000)
    private List<String> cc;

    @ApiModelProperty("设置密送人，可以为空")
    @Size(max = 1000)
    private List<String> bcc;

    public SendEmailByTemplateReq() {

    }

    /**
     * @param sysName      消息所属系统名称
     * @param templateName 消息模板名称
     * @param subject      设置邮件主题，不能为空
     * @param to           设置收件人，不能为空
     */
    public SendEmailByTemplateReq(String sysName, String templateName, String subject, String to) {
        this.sysName = sysName;
        this.templateName = templateName;
        this.to = new ArrayList<String>() {{
            add(to);
        }};
        this.subject = subject;
    }

    /**
     * @param sysName      消息所属系统名称
     * @param templateName 消息模板名称
     * @param params       模板需要参数
     * @param subject      设置邮件主题，不能为空
     * @param to           设置收件人，不能为空
     */
    public SendEmailByTemplateReq(String sysName, String templateName, Map<String, Object> params, String subject, String... to) {
        this.sysName = sysName;
        this.templateName = templateName;
        this.params = params;
        this.to = Arrays.asList(to);
        this.subject = subject;
    }
}
