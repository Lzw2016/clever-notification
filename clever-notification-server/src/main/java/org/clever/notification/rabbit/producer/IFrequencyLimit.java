package org.clever.notification.rabbit.producer;

import org.clever.notification.model.BaseMessage;

/**
 * 限制消息发送频率(分钟，小时，天，周，月)<br />
 * 从发送者列表中排除发送频率超限的帐号<br />
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 20:44 <br/>
 */
public interface IFrequencyLimit {

    /**
     * 帐号是否发送频率超限
     *
     * @param sysName     系统名
     * @param messageType 消息类型
     * @param account     帐号
     * @return true:发送频率超限；false:发送频率没有超限
     */
    boolean frequencyLimit(String sysName, Integer messageType, String account);

    /**
     * 从发送者列表中排除发送频率超限的帐号
     *
     * @param message 消息
     * @return 除去黑名单接受者的消息
     */
    <T extends BaseMessage> T removeFrequencyLimit(T message);
}
