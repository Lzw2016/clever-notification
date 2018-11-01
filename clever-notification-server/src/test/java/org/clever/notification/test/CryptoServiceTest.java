package org.clever.notification.test;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.reflection.ReflectionsUtils;
import org.clever.notification.config.GlobalConfig;
import org.clever.notification.service.CryptoService;
import org.junit.Test;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 11:04 <br/>
 */
@Slf4j
public class CryptoServiceTest {

    @Test
    public void t01() {
        CryptoService cryptoService = new CryptoService();
        ReflectionsUtils.setFieldValue(cryptoService, "globalConfig", new GlobalConfig());
        String tmp = cryptoService.reqAesEncrypt("");
        log.info("### {} {}", tmp, cryptoService.reqAesDecrypt(tmp));
    }

    @Test
    public void t02() {
        CryptoService cryptoService = new CryptoService();
        ReflectionsUtils.setFieldValue(cryptoService, "globalConfig", new GlobalConfig());
        log.info("### {} ", cryptoService.dbAesDecrypt(""));
    }

    @Test
    public void t03() {
        CryptoService cryptoService = new CryptoService();
        ReflectionsUtils.setFieldValue(cryptoService, "globalConfig", new GlobalConfig());
        String password = cryptoService.reqAesDecrypt("");
        log.info("### {}", password);
        password = cryptoService.dbAesEncrypt(password);
        log.info("### {}", password);
        password = cryptoService.dbAesDecrypt(password);
        log.info("### {}", password);
    }
}
