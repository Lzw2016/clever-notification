package org.clever.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.exception.BusinessException;
import org.clever.notification.dto.request.SysBindEmailQueryReq;
import org.clever.notification.entity.SysBindEmail;
import org.clever.notification.mapper.SysBindEmailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 10:15 <br/>
 */
@Transactional(readOnly = true)
@Service
@Slf4j
public class ManageBySysBindEmailService {

    @Autowired
    private SysBindEmailMapper sysBindEmailMapper;
    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private SendEmailService sendEmailService;

    public IPage<SysBindEmail> findByPage(SysBindEmailQueryReq req) {
        Page<SysBindEmail> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setRecords(sysBindEmailMapper.findByPage(req, page));
        return page;
    }

    @Transactional
    public SysBindEmail addSysBindEmail(SysBindEmail sysBindEmail) {
        if (sysBindEmailMapper.existsAccount(sysBindEmail.getAccount()) >= 1) {
            throw new BusinessException("邮件帐号已经存在");
        }
        // 密码先解密再加密
        String password = cryptoService.reqAesDecrypt(sysBindEmail.getPassword());
        password = cryptoService.dbAesEncrypt(password);
        sysBindEmail.setPassword(password);
        sysBindEmailMapper.insert(sysBindEmail);
        sendEmailService.addSendMailUtils(sysBindEmail);
        return sysBindEmailMapper.selectById(sysBindEmail.getId());
    }
}
