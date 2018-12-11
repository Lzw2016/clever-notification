package org.clever.notification.entity;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-09-17 14:15 <br/>
 */
public class EnumConstant {

    /**
     * 消息类型，1：邮件
     */
    public static final Integer MessageType_1 = 1;

    /**
     * 消息类型，2：短信
     */
    public static final Integer MessageType_2 = 2;

    /**
     * 发送状态，1：发送中
     */
    public static final Integer SendState_1 = 1;

    /**
     * 发送状态，2：发送失败
     */
    public static final Integer SendState_2 = 2;

    /**
     * 发送状态，3：发送成功
     */
    public static final Integer SendState_3 = 3;

    /**
     * 接收状态，1：未知
     */
    public static final Integer ReceiveState_1 = 1;

    /**
     * 接收状态，2：接收失败
     */
    public static final Integer ReceiveState_2 = 2;

    /**
     * 接收状态，3：接收成功
     */
    public static final Integer ReceiveState_3 = 3;

    /**
     * 全局系统名
     */
    public static final String RootSysName = "root";

    /**
     * 是否启用，0：禁用
     */
    public static final Integer Enabled_0 = 0;

    /**
     * 是否启用，1：启用
     */
    public static final Integer Enabled_1 = 1;
}
