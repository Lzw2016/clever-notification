package org.clever.notification.job;

import lombok.extern.slf4j.Slf4j;
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
public class LoadFrequencyLimit {

    @Autowired
    private FrequencyLimitService frequencyLimitService;

    /**
     * 定时加载黑名单(直接修改数据库或集群时有用)
     */
    @Scheduled(cron = "0/5 * * * * ?") // 测试
//    @Scheduled(cron = "0 0/30 * * * ?")
    public void loadBlackList() {
        frequencyLimitService.load();
    }
}
