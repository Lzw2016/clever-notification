package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.javamail.EmailServerHostUtils;
import org.clever.common.utils.javamail.SpringSendMailUtils;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.MessageSendLog;
import org.clever.notification.entity.SysBindEmail;
import org.clever.notification.mapper.MessageSendLogMapper;
import org.clever.notification.mapper.SysBindEmailMapper;
import org.clever.notification.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 发送邮件工具
 * 作者： lzw<br/>
 * 创建时间：2018-10-30 21:27 <br/>
 */
@Service
@Slf4j
public class SendEmailService {
    private static final String RootSysName = "root";

    /**
     * 发送邮件工具集合(线程安全) sysName -> SpringSendMailUtils
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<SpringSendMailUtils>> SendMailUtilsMap = new ConcurrentHashMap<>();

    /**
     * 用来轮询访问发送邮件工具(线程安全)  sysName -> 轮询index
     */
    private static final ConcurrentHashMap<String, AtomicInteger> PollingSendMailUtils = new ConcurrentHashMap<>();

    @Autowired
    private SysBindEmailMapper sysBindEmailMapper;
    @Autowired
    private MessageSendLogMapper messageSendLogMapper;
    @Autowired
    private CryptoService cryptoService;

    private SpringSendMailUtils newSpringSendMailUtils(SysBindEmail sysBindEmail) {
        log.info("### 初始化 JavaMailSender {} -> {}", sysBindEmail.getSysName(), sysBindEmail.getAccount());
        // 创建 JavaMailSender
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.getJavaMailProperties().setProperty("mail.smtp.auth", "true");
        javaMailSender.getJavaMailProperties().setProperty("mail.smtp.timeout", "20000");
        javaMailSender.setDefaultEncoding("UtF-8");
        if (StringUtils.isBlank(sysBindEmail.getSmtpHost())) {
            javaMailSender.setHost(EmailServerHostUtils.getEmailSmtpHost(sysBindEmail.getAccount()));
        } else {
            javaMailSender.setHost(sysBindEmail.getSmtpHost());
        }
        // javaMailSender.setPort(3306);
        javaMailSender.setUsername(sysBindEmail.getAccount());
        // 密码解密
        javaMailSender.setPassword(cryptoService.dbAesDecrypt(sysBindEmail.getPassword()));
        return new SpringSendMailUtils(javaMailSender);
    }

    public void addSendMailUtils(SysBindEmail sysBindEmail) {
        SpringSendMailUtils springSendMailUtils = newSpringSendMailUtils(sysBindEmail);
        // 各个系统自己的帐号
        List<SpringSendMailUtils> mailUtils = SendMailUtilsMap.computeIfAbsent(sysBindEmail.getSysName(), k -> new CopyOnWriteArrayList<>());
        // mailUtils.stream().filter(springSendMailUtils1 -> springSendMailUtils.getJavaMailSender().get).findFirst()
        mailUtils.add(springSendMailUtils);
        PollingSendMailUtils.computeIfAbsent(sysBindEmail.getSysName(), k -> new AtomicInteger(0));
    }

    @PostConstruct
    private void init() {
        List<SysBindEmail> sysBindEmailList = sysBindEmailMapper.getAllEnabled();
        for (SysBindEmail sysBindEmail : sysBindEmailList) {
            addSendMailUtils(sysBindEmail);
        }
        if (PollingSendMailUtils.size() <= 0) {
            log.warn("### 未配置发送邮件帐号密码");
        }
    }

    /**
     * 获取发送邮件工具
     *
     * @param sysName 系统名称
     */
    private SpringSendMailUtils getSendMailUtils(String sysName) {
        List<SpringSendMailUtils> mailUtils = SendMailUtilsMap.get(sysName);
        if (mailUtils == null || mailUtils.size() <= 0) {
            mailUtils = SendMailUtilsMap.get(RootSysName);
        }
        SpringSendMailUtils springSendMailUtils = null;
        if (mailUtils != null && mailUtils.size() > 0) {
            // 轮询返回
            AtomicInteger atomicInteger = PollingSendMailUtils.computeIfAbsent(sysName, k -> new AtomicInteger(0));
            int index = atomicInteger.getAndIncrement();
            if (index >= mailUtils.size()) {
                atomicInteger.set(0);
            }
            index = index % mailUtils.size();
            log.debug("### 取值下标 index={}", index);
            springSendMailUtils = mailUtils.get(index);
        }
        if (springSendMailUtils == null) {
            throw new BusinessException("系统[" + sysName + "]没有配置发送邮件帐号密码");
        }
        return springSendMailUtils;
    }

    /**
     * 发送邮件工具是否存在
     */
    public boolean sendMailUtilsNotExists(String sysName) {
        List<SpringSendMailUtils> mailUtils = SendMailUtilsMap.get(sysName);
        if (mailUtils == null || mailUtils.size() <= 0) {
            mailUtils = SendMailUtilsMap.get(RootSysName);
        }
        return (mailUtils == null || mailUtils.size() <= 0);
    }

    /**
     * 发送邮件
     */
    @Transactional
    public boolean sendEmail(EmailMessage emailMessage) {
        // 记录发送日志
        MessageSendLog messageSendLog = emailMessage.createMessageSendLog();
        messageSendLog.setSendTime(new Date());
        messageSendLogMapper.insert(messageSendLog);
        try {
            SpringSendMailUtils springSendMailUtils = getSendMailUtils(emailMessage.getSysName());
            springSendMailUtils.sendMimeMessage(
                    emailMessage.getTo().toArray(new String[]{}),
                    emailMessage.getSubject(),
                    emailMessage.getContent(),
                    null,
                    null,
                    emailMessage.getCc() == null ? null : emailMessage.getCc().toArray(new String[]{}),
                    emailMessage.getBcc() == null ? null : emailMessage.getBcc().toArray(new String[]{}),
                    null,
                    null
            );
        } catch (Throwable e) {
            log.error("发送邮件失败", e);
            // 更新发送日志 - 失败
            MessageSendLog update = new MessageSendLog();
            update.setId(messageSendLog.getId());
            update.setSendState(EnumConstant.SendState_2);
            update.setFailReason(StringUtils.mid(e.getMessage(), 0, 511));
            update.setUseTime(new Date().getTime() - messageSendLog.getSendTime().getTime());
            update.setUpdateAt(new Date());
            messageSendLogMapper.updateById(update);
            return false;
        }
        // 更新发送日志 - 成功
        MessageSendLog update = new MessageSendLog();
        update.setId(messageSendLog.getId());
        update.setSendState(EnumConstant.SendState_3);
        update.setUseTime(new Date().getTime() - messageSendLog.getSendTime().getTime());
        update.setUpdateAt(new Date());
        messageSendLogMapper.updateById(update);
        return true;
    }
}
