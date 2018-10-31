package org.clever.notification.rabbit;

import org.clever.notification.model.BaseMessage;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 10:17 <br/>
 */
public abstract class BaseSendMessage<T extends BaseMessage> implements ISendMessage<T> {

    /**
     * 只需要发送消息即可(异步)<br />
     * 1. 消息ID已经生成<br />
     * 2. 消息内容已经生成<br />
     * 3. 已经验证消息<br />
     */
    protected abstract void internalAsyncSend(T baseMessage);

    /**
     * 只需要发送消息即可(同步)<br />
     * 1. 消息ID已经生成<br />
     * 2. 消息内容已经生成<br />
     * 3. 已经验证消息<br />
     */
    protected abstract void internalSend(T baseMessage);

    /**
     * 生成下一个消息ID
     */
    protected abstract Long nextId();

    /**
     * 发送消息之前处理
     */
    private void sendBefore(T baseMessage) {
        // 生成消息ID
        baseMessage.setSendId(nextId());
        // 生成消息内容
        baseMessage.generateContent();
        // 验证消息
        baseMessage.valid();
    }

    @Override
    public T send(T baseMessage) {
        sendBefore(baseMessage);
        // 同步 发送消息
        internalSend(baseMessage);
        return baseMessage;
    }

    @Override
    public T asyncSend(T baseMessage) {
        sendBefore(baseMessage);
        // 异步 发送消息
        internalAsyncSend(baseMessage);
        return baseMessage;
    }
}
