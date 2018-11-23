package org.clever.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.SysBindEmailQueryReq;
import org.clever.notification.dto.request.SysBindEmailUpdateReq;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.SysBindEmail;
import org.clever.notification.mapper.SysBindEmailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 10:15 <br/>
 */
@Transactional(readOnly = true)
@Service
@Slf4j
public class SysBindEmailService {

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

    @Transactional
    public SysBindEmail updateSysBindEmail(Long id, SysBindEmailUpdateReq req) {
        SysBindEmail oldSysBindEmail = sysBindEmailMapper.selectById(id);
        if (oldSysBindEmail == null) {
            throw new BusinessException("更新数据不存在");
        }
        // 校验帐号
        if (StringUtils.isNotBlank(req.getAccount()) && !Objects.equals(req.getAccount(), oldSysBindEmail.getAccount())) {
            SysBindEmail tmp = sysBindEmailMapper.getByAccount(req.getAccount());
            if (tmp != null && !Objects.equals(oldSysBindEmail.getId(), tmp.getId())) {
                throw new BusinessException("邮箱帐号已经存在");
            }
        }
        // 修改密码-解密再加密
        if (StringUtils.isNotBlank(req.getPassword())) {
            String password = cryptoService.reqAesDecrypt(req.getPassword());
            password = cryptoService.dbAesEncrypt(password);
            req.setPassword(password);
        }
        SysBindEmail newSysBindEmail = BeanMapper.mapper(req, SysBindEmail.class);
        newSysBindEmail.setId(oldSysBindEmail.getId());
        sysBindEmailMapper.updateById(newSysBindEmail);
        newSysBindEmail = sysBindEmailMapper.selectById(newSysBindEmail.getId());
        // 帐号修改了 - 更新帐号
        if (!Objects.equals(oldSysBindEmail.getSysName(), newSysBindEmail.getSysName())
                || !Objects.equals(oldSysBindEmail.getAccount(), newSysBindEmail.getAccount())
                || !Objects.equals(oldSysBindEmail.getPassword(), newSysBindEmail.getPassword())
                || !Objects.equals(oldSysBindEmail.getFromName(), newSysBindEmail.getFromName())
                || !Objects.equals(oldSysBindEmail.getSmtpHost(), newSysBindEmail.getSmtpHost())
                || !Objects.equals(oldSysBindEmail.getPop3Host(), newSysBindEmail.getPop3Host())
                || !Objects.equals(oldSysBindEmail.getEnabled(), newSysBindEmail.getEnabled())) {
            // 删除旧账号
            sendEmailService.delSendMailUtils(oldSysBindEmail);
            if (Objects.equals(newSysBindEmail.getEnabled(), EnumConstant.Enabled_1)) {
                // 增加新账号
                sendEmailService.addSendMailUtils(newSysBindEmail);
            }
        }
        return newSysBindEmail;
    }

    @Transactional
    public SysBindEmail delSysBindEmail(Long id) {
        SysBindEmail sysBindEmail = sysBindEmailMapper.selectById(id);
        if (sysBindEmail == null) {
            throw new BusinessException("删除数据不存在");
        }
        sendEmailService.delSendMailUtils(sysBindEmail);
        return sysBindEmail;
    }
}
