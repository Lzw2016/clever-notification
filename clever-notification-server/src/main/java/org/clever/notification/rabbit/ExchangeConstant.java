package org.clever.notification.rabbit;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 17:26 <br/>
 */
public class ExchangeConstant {

    /**
     * 消息交换器
     */
    public static final String MessageExchange = "message-notification";

    /**
     * 消息交换器 - 死信交换器
     */
    public static final String MessageExchangeDead = "message-notification-dead";
}
