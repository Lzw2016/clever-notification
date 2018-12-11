package org.clever.notification.service;

import com.alicom.mns.tools.DefaultAlicomMessagePuller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.notification.config.GlobalConfig;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.MessageSendLog;
import org.clever.notification.model.SmsMessage;
import org.clever.notification.send.IDistinctSendId;
import org.clever.notification.send.aliyun.sms.SmsApiUtils;
import org.clever.notification.send.aliyun.sms.SmsReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    private MessageSendLogService messageSendLogService;
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

    @PostConstruct
    private void init() {
        DefaultAlicomMessagePuller puller = new DefaultAlicomMessagePuller();
        //设置异步线程池大小及任务队列的大小，还有无数据线程休眠时间
        puller.setConsumeMinThreadSize(6);
        puller.setConsumeMaxThreadSize(16);
        puller.setThreadQueueSize(200);
        puller.setPullMsgThreadSize(1);
        //和服务端联调问题时开启,平时无需开启，消耗性能
        puller.openDebugLog(false);
        /*
         * 将messageType和queueName替换成您需要的消息类型名称和对应的队列名称
         * 云通信产品下所有的回执消息类型:
         * 1:短信回执：SmsReport，
         * 2:短息上行：SmsUp
         * 3:语音呼叫：VoiceReport
         * 4:流量直冲：FlowReport
         */
        //此处应该替换成相应产品的消息类型
        String messageType = "SmsReport";
        //在云通信页面开通相应业务消息后，就能在页面上获得对应的queueName,格式类似Alicom-Queue-xxxxxx-SmsReport
        String queueName = aliyunSmsConfig.getReceiveQueueName();
        try {
            puller.startReceiveMsg(
                    aliyunSmsConfig.getAccessKeyId(),
                    aliyunSmsConfig.getAccessKeySecret(),
                    messageType,
                    queueName,
                    new SmsReport(smsReceiveData -> {
                        log.info("### 更新短信发送状态 {}", smsReceiveData);
                        Long sendId = NumberUtils.toLong(smsReceiveData.getOutId(), -1L);
                        Integer receiveState = smsReceiveData.getSuccess() ? EnumConstant.ReceiveState_3 : EnumConstant.ReceiveState_2;
                        String receiveMsg = smsReceiveData.getErrMsg();
                        messageSendLogService.updateReceiveState(sendId, receiveState, receiveMsg);
                    }));
        } catch (Throwable e) {
            throw ExceptionUtils.unchecked(e);
        }
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
