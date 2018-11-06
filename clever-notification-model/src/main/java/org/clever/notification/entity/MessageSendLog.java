package org.clever.notification.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息发送日志(MessageSendLog)实体类
 *
 * @author lizw
 * @since 2018-10-30 19:29:29
 */
@Data
public class MessageSendLog implements Serializable {
    private static final long serialVersionUID = -42544269604253792L;
    /**
     * 主键id
     */
    private Long id;

    /**
     * 消息发送ID
     */
    private Long sendId;

    /**
     * 系统名称
     */
    private String sysName;

    /**
     * 消息类型，1：邮件；2：短信；...
     */
    private Integer messageType;

    /**
     * 消息模版名称
     */
    private String templateName;

    /**
     * 消息对象Json字符串
     */
    private String messageObject;

    /**
     * 发送状态，1：发送中；2：发送失败；3：发送成功
     */
    private Integer sendState;

    /**
     * 发送失败原因
     */
    private String failReason;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 发送消息耗时(毫秒)
     */
    private Long useTime;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}