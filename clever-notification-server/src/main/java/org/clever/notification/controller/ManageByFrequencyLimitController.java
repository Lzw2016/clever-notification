package org.clever.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.FrequencyLimitAddReq;
import org.clever.notification.dto.request.FrequencyLimitQueryReq;
import org.clever.notification.dto.request.FrequencyLimitUpdateReq;
import org.clever.notification.entity.FrequencyLimit;
import org.clever.notification.service.FrequencyLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-09 23:11 <br/>
 */
@Api(description = "管理发送限速配置")
@RestController
@RequestMapping("/api/manage")
public class ManageByFrequencyLimitController {

    @Autowired
    private FrequencyLimitService frequencyLimitService;

    @ApiOperation("分页查询限制消息发送频率")
    @GetMapping("/frequency_limit")
    public IPage<FrequencyLimit> findByPage(FrequencyLimitQueryReq queryReq) {
        return frequencyLimitService.findByPage(queryReq);
    }

    @ApiOperation("新增限制消息发送频率")
    @PostMapping("/frequency_limit")
    public FrequencyLimit addFrequencyLimit(@RequestBody @Validated FrequencyLimitAddReq addReq) {
        FrequencyLimit frequencyLimit = BeanMapper.mapper(addReq, FrequencyLimit.class);
        return frequencyLimitService.addFrequencyLimit(frequencyLimit);
    }

    @ApiOperation("更新限制消息发送频率")
    @PutMapping("/frequency_limit/{id}")
    public FrequencyLimit updateFrequencyLimit(@PathVariable("id") Long id, @RequestBody @Validated FrequencyLimitUpdateReq updateReq) {
        return frequencyLimitService.updateFrequencyLimit(id, updateReq);
    }

    @ApiOperation("删除限制消息发送频率")
    @DeleteMapping("/frequency_limit/{id}")
    public FrequencyLimit delFrequencyLimit( @PathVariable("id") Long id) {
        return frequencyLimitService.delFrequencyLimit(id);
    }
}
