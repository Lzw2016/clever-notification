package org.clever.notification.send.aliyun.sms;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-12-11 14:22 <br/>
 */
public interface SmsReportHandle {

    void handle(SmsReport.SmsReceiveData smsReceiveData);
}
