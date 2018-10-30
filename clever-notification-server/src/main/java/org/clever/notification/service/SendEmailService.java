package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.javamail.EmailServerHostUtils;
import org.clever.common.utils.javamail.SpringSendMailUtils;
import org.clever.notification.entity.SysBindEmail;
import org.clever.notification.mapper.SysBindEmailMapper;
import org.clever.notification.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发送邮件工具
 * 作者： lzw<br/>
 * 创建时间：2018-10-30 21:27 <br/>
 */
@Transactional(readOnly = true)
@Service
@Slf4j
public class SendEmailService {

    /**
     * 发送邮件工具集合 sysName -> SpringSendMailUtils
     */
    private static final Map<String, List<SpringSendMailUtils>> SendMailUtilsMap = new ConcurrentHashMap<>();

    /**
     * 系统默认发送邮件工具
     */
    private static SpringSendMailUtils RootSendMailUtils;

    @Autowired
    private SysBindEmailMapper sysBindEmailMapper;

    @PostConstruct
    protected void init() {
        List<SysBindEmail> sysBindEmailList = sysBindEmailMapper.getAllEnabled();
        for (SysBindEmail sysBindEmail : sysBindEmailList) {
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
            javaMailSender.setPassword(sysBindEmail.getPassword());
            SpringSendMailUtils springSendMailUtils = new SpringSendMailUtils(javaMailSender);
            // 判断 - 系统默认发送邮件工具
            if (Objects.equals("root", sysBindEmail.getSysName())) {
                RootSendMailUtils = springSendMailUtils;
                continue;
            }
            List<SpringSendMailUtils> mailUtils = SendMailUtilsMap.computeIfAbsent(sysBindEmail.getSysName(), k -> new ArrayList<>());
            mailUtils.add(springSendMailUtils);
            log.info("### 初始化 JavaMailSender {} -> {}", sysBindEmail.getSysName(), sysBindEmail.getAccount());
        }
    }

    /**
     * 获取发送邮件工具
     *
     * @param sysName 系统名称
     */
    private SpringSendMailUtils getSendMailUtils(String sysName) {
        List<SpringSendMailUtils> mailUtils = SendMailUtilsMap.get(sysName);
        SpringSendMailUtils springSendMailUtils = null;
        if (mailUtils != null && mailUtils.size() > 0) {
            // TODO 轮询返回
            springSendMailUtils = mailUtils.get(0);
        }
        if (springSendMailUtils == null) {
            springSendMailUtils = RootSendMailUtils;
        }
        if (springSendMailUtils == null) {
            throw new BusinessException("系统[" + sysName + "]没有配置发送邮件帐号密码");
        }
        return springSendMailUtils;
    }

    /**
     * 发送邮件
     */
    public void senEmail(EmailMessage emailMessage) {
        SpringSendMailUtils springSendMailUtils = getSendMailUtils(emailMessage.getSysName());
        // TODO FreeMarker生成邮件内容
        springSendMailUtils.sendMimeMessage(
                emailMessage.getTo().toArray(new String[]{}),
                emailMessage.getSubject(),
                emailMessage.getContent(),
                null,
                null,
                emailMessage.getCc().toArray(new String[]{}),
                emailMessage.getBcc().toArray(new String[]{}),
                null,
                null
        );
    }
}
