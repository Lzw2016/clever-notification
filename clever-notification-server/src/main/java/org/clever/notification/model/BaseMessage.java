package org.clever.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.clever.notification.utils.StringTemplateUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 基础消息
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 16:40 <br/>
 */
@ToString
@EqualsAndHashCode
public abstract class BaseMessage implements Serializable {

    /**
     * 消息发送ID (全局唯一)
     */
    @Getter
    @Setter
    private Long sendId;

    /**
     * 消息所属系统名称
     */
    @Getter
    @Setter
    private String sysName;

    /**
     * 消息模板名称
     */
    @Getter
    @Setter
    private String templateName;

    /**
     * 模板需要参数
     */
    @Getter
    @Setter
    private Map<String, Object> params;

    /**
     * 设置邮件内容，不能为空
     */
    @Getter
    protected String content;

    /**
     * 验证消息配置是否正确
     */
    public abstract void valid();

    /**
     * 根据“消息模版内容”、“模板需要参数”生成消息内容<br />
     * 优先取content，content为空则调用模板生成content<br />
     * <b>注意：只有消息内容不为空才会生成消息内容，否则直接返回</b>
     */
    @JsonIgnore
    public String generateContent() {
        if (StringUtils.isBlank(content)) {
            content = StringTemplateUtils.getContentByTemplateName(templateName, params);
        }
        return content;
    }
}
