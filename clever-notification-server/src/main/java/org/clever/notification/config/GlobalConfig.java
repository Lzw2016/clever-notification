package org.clever.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2017-12-04 12:44 <br/>
 */
@Component
@ConfigurationProperties(prefix = "clever.config")
@Data
public class GlobalConfig {

    /**
     * 雪花算法配置
     */
    private SnowFlakeConfig snowFlakeConfig = new SnowFlakeConfig();

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
}
