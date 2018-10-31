package org.clever.notification.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接收者黑名单(ReceiverBlackList)实体类
 *
 * @author lizw
 * @since 2018-10-30 19:29:29
 */
@Data
public class ReceiverBlackList implements Serializable {
    private static final long serialVersionUID = -72615627522200871L;
    /**
     * 主键id
     */
    private Long id;

    /**
     * 系统名称(为空就是全局黑名单)
     */
    private String sysName;

    /**
     * 消息类型，1：邮件；2：短信；...
     */
    private Integer messageType;

    /**
     * 黑名单帐号
     */
    private String account;

    /**
     * 是否启用，0：禁用；1：启用
     */
    private Integer enabled;

    /**
     * 黑名单帐号过期时间(到期自动禁用)
     */
    private Date expiredTime;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}