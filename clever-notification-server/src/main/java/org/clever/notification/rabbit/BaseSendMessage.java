package org.clever.notification.rabbit;

import org.clever.notification.model.BaseMessage;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 10:17 <br/>
 */
public abstract class BaseSendMessage<T extends BaseMessage> implements ISendMessage<T> {

    /**
     * 只需要发送消息即可<br />
     * 1. 消息ID已经生成<br />
     * 2. 消息内容已经生成<br />
     */
    public abstract void internalSend(T baseMessage);

    /**
     * 生成下一个消息ID
     */
    public abstract Long nextId();

    /**
     * @param baseMessage 发送的消息
     */
    @Override
    public T send(T baseMessage) {
        // 生成消息ID
        baseMessage.setSendId(nextId());
        // 生成消息内容
        baseMessage.generateContent();
        // 发送消息
        internalSend(baseMessage);
        return baseMessage;
    }
}
