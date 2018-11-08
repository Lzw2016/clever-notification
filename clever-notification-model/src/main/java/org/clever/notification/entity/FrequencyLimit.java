package org.clever.notification.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 限制消息发送频率(分钟，小时，天，周，月)(FrequencyLimit)实体类
 *
 * @author lizw
 * @since 2018-11-06 21:29:26
 */
@Data
public class FrequencyLimit implements Serializable {
    private static final long serialVersionUID = -93945758736087548L;
    /**
     * 主键id
     */
    private Long id;

    /**
     * 系统名称(全局使用“root”名称)
     */
    private String sysName;

    /**
     * 消息类型，1：邮件；2：短信；...(消息类型为空表示对系统的限制)
     */
    private Integer messageType;

    /**
     * 限速帐号(帐号为空表示对消息类型的限制)
     */
    private String account;

    /**
     * 是否启用，0：禁用；1：启用
     */
    private Integer enabled;

    /**
     * 限速配置过期时间(到期自动禁用)
     */
    private Date expiredTime;

    /**
     * 一分钟内的发送次数(小于等于0表示不限制)
     */
    private Integer minutesCount;

    /**
     * 一小时内的发送次数(小于等于0表示不限制)
     */
    private Integer hoursCount;

    /**
     * 一天内的发送次数(小于等于0表示不限制)
     */
    private Integer daysCount;

    /**
     * 一周内的发送次数(小于等于0表示不限制)
     */
    private Integer weeksCount;

    /**
     * 一月内的发送次数(小于等于0表示不限制)
     */
    private Integer monthsCount;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;
}