package org.clever.notification.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 基础消息
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 16:40 <br/>
 */
@Data
public class BaseMessage implements Serializable {

    /**
     * 消息发送ID (全局唯一)
     */
    private Long sendId;

    /**
     * 消息所属系统名称
     */
    private String sysName;

    /**
     * 消息模板名称
     */
    private String templateName;

    /**
     * 模板需要参数
     */
    private Map<String, Object> params;
}
