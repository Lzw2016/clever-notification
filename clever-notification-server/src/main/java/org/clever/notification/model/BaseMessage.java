package org.clever.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.MessageSendLog;
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
     * 异步发送时的回调Url接口
     */
    @Getter
    @Setter
    private String asyncCallBack;

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
     * 是否已经使用“模板需要参数”填充模版
     */
    @Getter
    private boolean paramsFillContent = false;

    /**
     * 验证消息配置是否正确
     */
    public void valid() {
        if (StringUtils.isBlank(sysName)) {
            throw new BusinessException("消息所属系统名称，不能为空");
        }
        if (StringUtils.isBlank(templateName) && StringUtils.isBlank(content)) {
            throw new BusinessException("消息模版和者消息内容不能同时为空");
        }
    }

    /**
     * 根据“消息模版内容”、“模板需要参数”生成消息内容<br />
     * 优先取content，content为空则调用模板生成content<br />
     * <b>注意：只有消息内容不为空才会生成消息内容，否则直接返回</b>
     */
    @JsonIgnore
    public String generateContent() {
        if (paramsFillContent) {
            return content;
        }
        if (StringUtils.isNotBlank(content)) {
            content = StringTemplateUtils.getContentByStrTemplate(content, params);
        } else {
            content = StringTemplateUtils.getContentByTemplateName(templateName, params);
        }
        paramsFillContent = true;
        return content;
    }

    /**
     * 创建消息日志
     */
    protected MessageSendLog createMessageSendLog() {
        MessageSendLog messageSendLog = new MessageSendLog();
        messageSendLog.setSendId(getSendId());
        messageSendLog.setSysName(getSysName());
        messageSendLog.setTemplateName(getTemplateName());
        messageSendLog.setMessageObject(JacksonMapper.nonEmptyMapper().toJson(this));
        messageSendLog.setSendState(EnumConstant.SendState_1);
        return messageSendLog;
    }
}
