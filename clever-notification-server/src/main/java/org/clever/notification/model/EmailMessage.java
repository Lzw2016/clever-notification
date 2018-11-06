package org.clever.notification.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.MessageSendLog;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 邮件消息
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 16:56 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmailMessage extends BaseMessage {

    /**
     * 设置收件人，不能为空
     */
    private List<String> to;

    /**
     * 设置邮件主题，不能为空
     */
    private String subject;

    /**
     * 设置抄送人，可以为空
     */
    private List<String> cc;

    /**
     * 设置密送人，可以为空
     */
    private List<String> bcc;

    /**
     * 设置邮件回复人，可以为空
     */
    private String replyTo;

    /**
     * 设置发送时间，可以为空
     */
    private Date sentDate;

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
        // 删除重复 删除空
        if (to != null && to.size() > 0) {
            to = to.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        }
        if (cc != null && cc.size() > 0) {
            cc = cc.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        }
        if (bcc != null && bcc.size() > 0) {
            bcc = bcc.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        }
        // 校验
        if (to == null || to.size() <= 0) {
            throw new BusinessException("设置收件人，不能为空");
        }
        if (StringUtils.isBlank(subject)) {
            throw new BusinessException("设置邮件主题，不能为空");
        }
    }

    @Override
    public MessageSendLog createMessageSendLog() {
        MessageSendLog messageSendLog = super.createMessageSendLog();
        messageSendLog.setMessageType(EnumConstant.MessageType_1);
        return messageSendLog;
    }
}
