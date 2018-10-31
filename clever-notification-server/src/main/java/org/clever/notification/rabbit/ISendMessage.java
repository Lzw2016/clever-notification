package org.clever.notification.rabbit;

import org.clever.notification.model.BaseMessage;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 10:15 <br/>
 */
public interface ISendMessage<T extends BaseMessage> {

    /**
     * 发送消息
     *
     * @param baseMessage 发送的消息
     * @return 发送的消息
     */
    T send(T baseMessage);
}
