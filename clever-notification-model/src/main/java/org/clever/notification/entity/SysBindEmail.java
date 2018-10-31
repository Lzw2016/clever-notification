package org.clever.notification.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统邮件发送者帐号(SysBindEmail)实体类
 *
 * @author lizw
 * @since 2018-10-30 19:29:29
 */
@Data
public class SysBindEmail implements Serializable {
    private static final long serialVersionUID = 436609706582873295L;
    /**
     * 主键id
     */
    private Long id;

    /**
     * 系统名称(全局使用“root”名称)
     */
    private String sysName;

    /**
     * 发送人的邮箱帐号
     */
    private String account;

    /**
     * 发送人的邮箱密码
     */
    private String password;

    /**
     * 发送人的名称
     */
    private String fromName;

    /**
     * SMTP服务器地址
     */
    private String smtpHost;

    /**
     * POP3服务器地址
     */
    private String pop3Host;

    /**
     * 是否启用，0：禁用；1：启用
     */
    private Integer enabled;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}