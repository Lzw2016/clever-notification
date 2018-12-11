package org.clever.notification.send.aliyun.sms;

import com.alicom.mns.tools.MessageListener;
import com.aliyun.mns.model.Message;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.mapper.JacksonMapper;

import java.io.Serializable;
import java.util.Date;

/**
 * 阿里云短信-通过订阅SmsReport短信状态报告，可以获知每条短信的发送情况，了解短信是否达到终端用户的状态与相关信息
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2018-12-10 15:49 <br/>
 */
@Slf4j
public class SmsReport implements MessageListener {

    private SmsReportHandle smsReportHandle;

    public SmsReport(SmsReportHandle smsReportHandle) {
        super();
        this.smsReportHandle = smsReportHandle;
    }

    @Override
    public boolean dealMessage(Message message) {
        String res = "handle=" + message.getReceiptHandle() + " | "
                + "body=" + message.getMessageBodyAsString() + " | "
                + "id=" + message.getMessageId() + " | "
                + "dequeue count=" + message.getDequeueCount();
        log.info("### 短信回执 {}", res);
        String json = message.getMessageBodyAsString();
        if (StringUtils.isBlank(json) || smsReportHandle == null) {
            return true;
        }
        try {
            SmsReceiveData smsReceiveData = JacksonMapper.nonEmptyMapper().fromJson(json, SmsReceiveData.class);
            // 业务处理
            smsReportHandle.handle(smsReceiveData);
        } catch (Throwable e) {
            //return false,这样消息不会被delete掉，而会根据策略进行重推
            return false;
        }
        //返回true, SDK将调用MNS的delete方法将消息从队列中删除掉
        return true;
    }

    @Data
    public static class SmsReceiveData implements Serializable {
        /**
         * 手机号码
         */
        @JsonProperty("phone_number")
        private String phoneNumber;
        /**
         * 发送时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("send_time")
        private Date sendTime;
        /**
         * 状态报告时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("report_time")
        private Date reportTime;
        /**
         * 是否接收成功
         */
        @JsonProperty("success")
        private Boolean success;
        /**
         * 状态报告编码
         */
        @JsonProperty("err_code")
        private String errCode;
        /**
         * 状态报告说明
         */
        @JsonProperty("err_msg")
        private String errMsg;
        /**
         * 短信长度
         */
        @JsonProperty("sms_size")
        private Integer smsSize;
        /**
         * 发送序列号
         */
        @JsonProperty("biz_id")
        private String bizId;
        /**
         * 用户序列号
         */
        @JsonProperty("out_id")
        private String outId;
    }
}
