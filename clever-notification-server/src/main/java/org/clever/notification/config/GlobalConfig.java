package org.clever.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2017-12-04 12:44 <br/>
 */
@Component
@ConfigurationProperties(prefix = "clever.config")
@Data
public class GlobalConfig {

    /**
     * 密码AES加密 key(Hex编码) -- 请求数据，与前端一致
     */
    private String reqPasswordAesKey = "636c657665722d736563757269747921";

    /**
     * 密码AES加密 iv(Hex编码) -- 请求数据，与前端一致
     */
    private String reqPasswordAesIv = "f0021ea5a06d5a7bade961afe47e9ad1";

    /**
     * 密码AES加密 key(Hex编码) -- 数据存储到数据库前使用
     */
    private String dbPasswordAesKey = "636c657665122d736563757269747921";

    /**
     * 密码AES加密 iv(Hex编码) -- 数据存储到数据库前使用
     */
    private String dbPasswordAesIv = "f00211a5a06d5a7bade961afe47e9ad1";

    /**
     * 发送短信模拟(测试使用)
     */
    private boolean smsMock = false;

    /**
     * 雪花算法配置
     */
    private SnowFlakeConfig snowFlakeConfig;

    /**
     * 消息SendId去重的SendId保留最大时间(单位秒，默认两小时)
     */
    private long distinctSendIdMaxTime = 60 * 60 * 2;

    /**
     * 阿里云Sms配置
     */
    private AliyunSmsConfig aliyunSmsConfig;

    @Data
    public static class SnowFlakeConfig {

        /**
         * 数据库中心 0 -- 32
         */
        private long dataCenterId = 0;

        /**
         * 机器ID 0 -- 32
         */
        private long machineId = 0;
    }

    @Data
    public static class AliyunSmsConfig {
        private String accessKeyId;
        private String accessKeySecret;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 模版名称
         */
        private List<String> templateNames;
    }
}
