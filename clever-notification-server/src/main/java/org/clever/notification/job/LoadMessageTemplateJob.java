package org.clever.notification.job;

import lombok.extern.slf4j.Slf4j;
import org.clever.notification.service.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 16:45 <br/>
 */
@Component
@Slf4j
public class LoadMessageTemplateJob {

    @Autowired
    private MessageTemplateService messageTemplateService;

    /**
     * 定时加载消息模版(直接修改数据库或集群时有用)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void loadMessageTemplate() {
        messageTemplateService.load();
    }
}
