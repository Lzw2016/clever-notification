package org.clever.notification.rabbit;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 17:19 <br/>
 */
public class RoutingKeyConstant {

    /**
     * 邮件路由key
     */
    public static final String EmailMessageKey = "notification.email.#";

    /**
     * 短信路由key
     */
    public static final String SmsMessage = "notification.sms.#";

}
