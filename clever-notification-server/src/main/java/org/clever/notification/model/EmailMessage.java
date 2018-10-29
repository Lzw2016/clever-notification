package org.clever.notification.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

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
     * 设置邮件内容，不能为空
     */
    private String content;

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
     * 验证消息配置是否正确
     */
    public void valid() {
        // TODO 验证
    }
}
