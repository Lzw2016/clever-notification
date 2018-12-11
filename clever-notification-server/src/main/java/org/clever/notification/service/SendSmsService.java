package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.notification.config.GlobalConfig;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.MessageSendLog;
import org.clever.notification.mapper.SysBindEmailMapper;
import org.clever.notification.model.SmsMessage;
import org.clever.notification.send.IDistinctSendId;
import org.clever.notification.send.aliyun.sms.SmsApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 发送短信工具
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 16:41 <br/>
 */
@Service
@Slf4j
public class SendSmsService {

    @Autowired
    private SysBindEmailMapper sysBindEmailMapper;
    @Autowired
    private MessageSendLogService messageSendLogService;
    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private IDistinctSendId distinctSendId;

    private final SmsApiUtils smsApiUtils;

    private final GlobalConfig.AliyunSmsConfig aliyunSmsConfig;

    protected SendSmsService(GlobalConfig globalConfig) {
        aliyunSmsConfig = globalConfig.getAliyunSmsConfig();
        if (aliyunSmsConfig == null || StringUtils.isBlank(aliyunSmsConfig.getAccessKeyId()) || StringUtils.isBlank(aliyunSmsConfig.getAccessKeySecret())) {
            throw new IllegalArgumentException("阿里云短信配置不能为空");
        }
        smsApiUtils = new SmsApiUtils(aliyunSmsConfig.getAccessKeyId(), aliyunSmsConfig.getAccessKeySecret());
    }

    /**
     * 发送短信
     */
    @Transactional
    public boolean sendSms(SmsMessage smsMessage) {
        // 记录发送日志
        MessageSendLog messageSendLog = smsMessage.createMessageSendLog();
        messageSendLog.setSendTime(new Date());
        messageSendLogService.addMessageSendLog(messageSendLog);
        try {
            smsApiUtils.sendSms(
                    smsMessage.getTo(),
                    aliyunSmsConfig.getSignName(),
                    smsMessage.getTemplateName(),
                    smsMessage.getSendId().toString(),
                    smsMessage.getParams()
            );
            distinctSendId.addSendId(smsMessage.getSendId());
        } catch (Throwable e) {
            log.error("发送短信失败", e);
            // 更新发送日志 - 失败
            MessageSendLog update = new MessageSendLog();
            update.setId(messageSendLog.getId());
            update.setSendState(EnumConstant.SendState_2);
            update.setFailReason(StringUtils.mid(e.getMessage(), 0, 511));
            update.setUseTime(new Date().getTime() - messageSendLog.getSendTime().getTime());
            messageSendLogService.updateMessageSendLog(update);
            return false;
        }
        // 更新发送日志 - 成功
        MessageSendLog update = new MessageSendLog();
        update.setId(messageSendLog.getId());
        update.setSendState(EnumConstant.SendState_3);
        update.setUseTime(new Date().getTime() - messageSendLog.getSendTime().getTime());
        messageSendLogService.updateMessageSendLog(update);
        return true;
    }
}
