package org.clever.notification.job;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.GlobalJob;
import org.clever.notification.service.ReceiverBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 18:15 <br/>
 */
@Component
@Slf4j
public class LoadBlackListJob extends GlobalJob {

    @Autowired
    private ReceiverBlackListService receiverBlackListService;


    /**
     * 定时加载黑名单(直接修改数据库或集群时有用)
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    @Override
    protected void internalExecute() {
        receiverBlackListService.load();
    }

    @Override
    protected void exceptionHandle(Throwable e) {
        log.info("### 定时加载黑名单异常", e);
    }
}
