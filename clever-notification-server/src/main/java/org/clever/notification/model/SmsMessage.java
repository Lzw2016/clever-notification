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
        // TODO 验证
    }
}
