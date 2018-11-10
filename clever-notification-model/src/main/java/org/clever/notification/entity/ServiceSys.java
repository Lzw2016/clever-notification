package org.clever.notification.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务系统(ServiceSys)实体类
 *
 * @author lizw
 * @since 2018-11-10 19:36:29
 */
@Data
public class ServiceSys implements Serializable {
    private static final long serialVersionUID = -16822705858294783L;
    /**
     * 主键id
     */
    private Long id;

    /**
     * 系统(或服务)名称
     */
    private String sysName;

    /**
     * 是否启用黑名单，0：禁用；1：启用
     */
    private Integer enableBlackList;

    /**
     * 黑名单最大数量(小于等于0表示不限制)
     */
    private Integer blackListMaxCount;

    /**
     * 是否启用发送评率限制，0：禁用；1：启用
     */
    private Integer enableFrequencyLimit;

    /**
     * 限制消息发送频率配置的最大数量(小于等于0表示不限制)
     */
    private Integer frequencyLimitMaxCount;

    /**
     * 是否启用，0：禁用；1：启用
     */
    private Integer enabled;

    /**
     * 说明
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;
}