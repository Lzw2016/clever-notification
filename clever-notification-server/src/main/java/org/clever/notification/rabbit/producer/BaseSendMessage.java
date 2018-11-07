package org.clever.notification.rabbit.producer;

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
     * 从发送者列表中排除黑名单 实现
     */
    protected abstract IExcludeBlackList getIExcludeBlackList();

    /**
     * 从发送者列表中排除发送频率超限的帐号 实现
     */
    protected abstract IFrequencyLimit getIFrequencyLimit();

    /**
     * 去重 Message SendId 实现
     */
    protected abstract IDistinctSendId getIDistinctSendId();

    /**
     * 发送消息之前处理
     */
    private T sendBefore(T baseMessage) {
        // 生成消息ID
        baseMessage.setSendId(nextId());
        // 生成消息内容
        baseMessage.generateContent();
        // 验证消息
        baseMessage.valid();
        // 黑名单限制
        baseMessage = getIExcludeBlackList().removeBlackList(baseMessage);
        // 发送频率限制
        baseMessage = getIFrequencyLimit().removeFrequencyLimit(baseMessage);
        return baseMessage;
    }

    @Override
    public T send(T baseMessage) {
        baseMessage = sendBefore(baseMessage);
        // 去重 Message SendId
        if (!getIDistinctSendId().existsSendId(baseMessage.getSendId())) {
            // 同步 发送消息
            internalSend(baseMessage);
        }
        return baseMessage;
    }

    @Override
    public T asyncSend(T baseMessage) {
        baseMessage = sendBefore(baseMessage);
        // 异步 发送消息
        internalAsyncSend(baseMessage);
        return baseMessage;
    }
}
