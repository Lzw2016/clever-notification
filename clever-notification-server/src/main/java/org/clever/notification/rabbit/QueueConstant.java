package org.clever.notification.rabbit;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 18:17 <br/>
 */
public class QueueConstant {

    /**
     * email 消息通知队列
     */
    public static final String EmailMessageQueue = "email-notification";

    /**
     * sms 消息通知队列
     */
    public static final String SmsMessageQueue = "sms-notification";

    /**
     * 消息死信队列
     */
    public static final String MessageQueueDead = "notification-dead";
}
