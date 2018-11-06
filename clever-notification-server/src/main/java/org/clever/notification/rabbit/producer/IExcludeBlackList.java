package org.clever.notification.rabbit.producer;

import org.clever.notification.model.BaseMessage;

/**
 * 从发送者列表中排除黑名单帐号
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 20:35 <br/>
 */
public interface IExcludeBlackList {

    /**
     * 帐号是否在黑名单列表中
     *
     * @param sysName     发送消息的系统名称
     * @param messageType 消息类型
     * @param account     帐号
     * @return true:在黑名单中；false:不在黑名单中
     */
    boolean inBlackList(String sysName, Integer messageType, String account);

    /**
     * 从发送者列表中排除黑名单帐号
     *
     * @param message 消息
     * @return 除去黑名单接受者的消息
     */
    <T extends BaseMessage> T removeBlackList(T message);
}
