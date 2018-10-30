package org.clever.notification.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * 消息模版(MessageTemplate)实体类
 *
 * @author lizw
 * @since 2018-10-30 19:29:29
 */
@Data
public class MessageTemplate implements Serializable {
    private static final long serialVersionUID = 278906045096582403L;
    /** 主键id */    
    private Long id;
    
    /** 模版名称 */    
    private String name;
    
    /** 标题 */    
    private String title;
    
    /** 模版内容 */    
    private String content;
    
    /** 模版消息示例 */    
    private String messageDemo;
    
    /** 是否启用，0：禁用；1：启用 */    
    private Integer enabled;
    
    /** 说明 */    
    private String description;
    
    /** 创建时间 */    
    private Date createAt;
    
    /** 更新时间 */    
    private Date updateAt;
    
}