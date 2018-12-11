package org.clever.notification.test;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.send.aliyun.sms.SmsApiUtils;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 16:29 <br/>
 */
@Slf4j
public class SmsApiUtilsTest {

    private static final SmsApiUtils smsApiUtils = new SmsApiUtils("LTAIZaucWkMKPwX4", "kvqrgsWgwNeNQuCbd4G7swyfVo1lhP");

    @Test
    public void test() {
        SendSmsResponse sendSmsResponse = smsApiUtils.sendSms(
                "13260658831",
                "Periscope",
                "SMS_149418310",
                "Q123",
                new HashMap<String, Object>() {{
                    put("code", "159753");
                }});
        log.info("### {}", sendSmsResponse.getRequestId());
        log.info("### {}", sendSmsResponse.getBizId());
        log.info("### {}", sendSmsResponse.getCode());
        log.info("### {}", sendSmsResponse.getMessage());
        smsApiUtils.close();
    }

    @Test
    public void test2() {
        QuerySendDetailsResponse querySendDetailsResponse = smsApiUtils.querySendDetails("13260658831", "472813044503905181^0", new Date(), 1, 100);
        log.info("### {}", querySendDetailsResponse.getRequestId());
        log.info("### {}", querySendDetailsResponse.getCode());
        log.info("### {}", querySendDetailsResponse.getMessage());
        log.info("### {}", querySendDetailsResponse.getTotalCount());
        for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs()) {
            log.info("### \t 手机号码 -> {}", smsSendDetailDTO.getPhoneNum());
            // 发送状态 1：等待回执，2：发送失败，3：发送成功
            log.info("### \t 发送状态  -> {}", smsSendDetailDTO.getSendStatus());
            log.info("### \t 运营商短信错误码 -> {}", smsSendDetailDTO.getErrCode());
            log.info("### \t 模板ID -> {}", smsSendDetailDTO.getTemplateCode());
            log.info("### \t 短信内容 -> {}", smsSendDetailDTO.getContent());
            log.info("### \t 发送时间 -> {}", smsSendDetailDTO.getSendDate());
            log.info("### \t 接收时间 -> {}", smsSendDetailDTO.getReceiveDate());
            log.info("### \t 外部流水扩展字段 -> {}", smsSendDetailDTO.getOutId());
            log.info("### ---------------------------------------");
        }
        smsApiUtils.close();
    }
}
