package org.clever.notification.job;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.GlobalJob;
import org.clever.notification.service.FrequencyLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-07 16:06 <br/>
 */
@Component
@Slf4j
public class LoadFrequencyLimitJob extends GlobalJob {

    @Autowired
    private FrequencyLimitService frequencyLimitService;

    /**
     * 定时加载限速配置(直接修改数据库或集群时有用)
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    @Override
    protected void internalExecute() {
        frequencyLimitService.load();
    }

    @Override
    protected void exceptionHandle(Throwable e) {
        log.info("### 定时加载限速配置异常", e);
    }
}
