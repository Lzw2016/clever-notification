package org.clever.notification.dto;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 21:27 <br/>
 */
public class PatternConstant {

    public static final String SysName_Pattern = "[a-zA-Z0-9\\u4e00-\\u9fa5()\\[\\]{}_-]{3,127}";

    public static final String Url_Pattern = "(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

    /**
     * 手机号正则表达式：1开始，长度11为数字
     */
    public static final String Telephone_Pattern = "1[0-9]{10}";
}
