package org.clever.notification.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
    private List<String> to;

    /**
     * 设置短信内容，不能为空
     */
    private String content;

    /**
     * 验证消息配置是否正确
     */
    public void valid() {
        // TODO 验证
    }
}
