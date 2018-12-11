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
        QuerySendDetailsResponse querySendDetailsResponse = smsApiUtils.querySendDetails("13260658831", "", new Date(), 1, 100);
        log.info("### {}", querySendDetailsResponse.getRequestId());
        log.info("### {}", querySendDetailsResponse.getCode());
        log.info("### {}", querySendDetailsResponse.getMessage());
        log.info("### {}", querySendDetailsResponse.getTotalCount());
        for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs()) {
            log.info("### \t -> {}", smsSendDetailDTO.getPhoneNum());
            log.info("### \t -> {}", smsSendDetailDTO.getSendStatus());
            log.info("### \t -> {}", smsSendDetailDTO.getErrCode());
            log.info("### \t -> {}", smsSendDetailDTO.getTemplateCode());
            log.info("### \t -> {}", smsSendDetailDTO.getContent());
            log.info("### \t -> {}", smsSendDetailDTO.getSendDate());
            log.info("### \t -> {}", smsSendDetailDTO.getReceiveDate());
            log.info("### \t -> {}", smsSendDetailDTO.getOutId());
            log.info("### ---------------------------------------");
        }
        smsApiUtils.close();
    }
}
