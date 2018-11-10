package org.clever.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.notification.job.LoadBlackListJob;
import org.clever.notification.job.LoadFrequencyLimitJob;
import org.clever.notification.job.LoadMessageTemplateJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 18:39 <br/>
 */
@Api(description = "系统调度任务")
@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private LoadBlackListJob loadBlackListJob;
    @Autowired
    private LoadFrequencyLimitJob loadFrequencyLimitJob;
    @Autowired
    private LoadMessageTemplateJob loadMessageTemplateJob;

    @ApiOperation("定时加载黑名单")
    @GetMapping("/load_black_list")
    public void loadBlackList() {
        loadBlackListJob.execute();
    }

    @ApiOperation("定时加载限速配置")
    @GetMapping("/load_frequency_limit")
    public void loadFrequencyLimit() {
        loadFrequencyLimitJob.execute();
    }

    @ApiOperation("定时加载消息模版")
    @GetMapping("/load_message_template")
    public void loadMessageTemplate() {
        loadMessageTemplateJob.execute();
    }
}
