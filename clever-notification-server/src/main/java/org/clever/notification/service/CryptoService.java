package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.codec.CryptoUtils;
import org.clever.common.utils.codec.EncodeDecodeUtils;
import org.clever.notification.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-01 10:42 <br/>
 */
@Component
@Slf4j
public class CryptoService {

    @Autowired
    private GlobalConfig globalConfig;

    /**
     * 请求数据 AES 加密
     *
     * @return Base64编码密码
     */
    public String reqAesEncrypt(String input) {
        byte[] passwordData = input.getBytes();
        byte[] key = EncodeDecodeUtils.decodeHex(globalConfig.getReqPasswordAesKey());
        byte[] iv = EncodeDecodeUtils.decodeHex(globalConfig.getReqPasswordAesIv());
        return EncodeDecodeUtils.encodeBase64(CryptoUtils.aesEncrypt(passwordData, key, iv));
    }

    /**
     * 请求数据 AES 解密
     *
     * @param input Base64编码密码
     */
    public String reqAesDecrypt(String input) {
        byte[] passwordData = EncodeDecodeUtils.decodeBase64(input);
        byte[] key = EncodeDecodeUtils.decodeHex(globalConfig.getReqPasswordAesKey());
        byte[] iv = EncodeDecodeUtils.decodeHex(globalConfig.getReqPasswordAesIv());
        return CryptoUtils.aesDecrypt(passwordData, key, iv);
    }

    /**
     * 数据存储到数据库前 AES 加密
     *
     * @return Base64编码密码
     */
    public String dbAesEncrypt(String input) {
        byte[] passwordData = input.getBytes();
        byte[] key = EncodeDecodeUtils.decodeHex(globalConfig.getDbPasswordAesKey());
        byte[] iv = EncodeDecodeUtils.decodeHex(globalConfig.getDbPasswordAesIv());
        return EncodeDecodeUtils.encodeBase64(CryptoUtils.aesEncrypt(passwordData, key, iv));
    }

    /**
     * 数据从数据库数据库读取后 AES 解密
     *
     * @param input Base64编码密码
     */
    public String dbAesDecrypt(String input) {
        byte[] passwordData = EncodeDecodeUtils.decodeBase64(input);
        byte[] key = EncodeDecodeUtils.decodeHex(globalConfig.getDbPasswordAesKey());
        byte[] iv = EncodeDecodeUtils.decodeHex(globalConfig.getDbPasswordAesIv());
        return CryptoUtils.aesDecrypt(passwordData, key, iv);
    }
}
