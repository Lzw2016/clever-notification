package org.clever.notification.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.MessageSendLog;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 17:09 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SmsMessage extends BaseMessage {

    /**
     * 接收手机号
     */
    private String to;

    /**
     * 设置消息内容
     *
     * @param content 消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 验证消息配置是否正确
     */
    @Override
    public void valid() {
        super.valid();
        if (StringUtils.isBlank(to)) {
            throw new BusinessException("接收手机号，不能为空");
        }
    }

    @Override
    public MessageSendLog createMessageSendLog() {
        MessageSendLog messageSendLog = super.createMessageSendLog();
        messageSendLog.setMessageType(EnumConstant.MessageType_2);
        return messageSendLog;
    }
}
