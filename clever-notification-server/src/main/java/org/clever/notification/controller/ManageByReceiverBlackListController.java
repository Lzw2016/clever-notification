package org.clever.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.ReceiverBlackListAddReq;
import org.clever.notification.dto.request.ReceiverBlackListQueryReq;
import org.clever.notification.dto.request.ReceiverBlackListUpdateReq;
import org.clever.notification.entity.ReceiverBlackList;
import org.clever.notification.service.ReceiverBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-09 23:10 <br/>
 */
@Api(description = "管理黑名单")
@RestController
@RequestMapping("/api/manage")
public class ManageByReceiverBlackListController {

    @Autowired
    private ReceiverBlackListService receiverBlackListService;

    @ApiOperation("分页查询黑名单")
    @GetMapping("/receiver_black_list")
    public IPage<ReceiverBlackList> findByPage(ReceiverBlackListQueryReq queryReq) {
        return receiverBlackListService.findByPage(queryReq);
    }

    @ApiOperation("增加黑名单")
    @PostMapping("/receiver_black_list")
    public ReceiverBlackList addReceiverBlackList(@RequestBody @Validated ReceiverBlackListAddReq addReq) {
        ReceiverBlackList receiverBlackList = BeanMapper.mapper(addReq, ReceiverBlackList.class);
        return receiverBlackListService.addReceiverBlackList(receiverBlackList);
    }

    @ApiOperation("更新黑名单")
    @PutMapping("/receiver_black_list/{id}")
    public ReceiverBlackList updateReceiverBlackList(@PathVariable("id") Long id, @RequestBody @Validated ReceiverBlackListUpdateReq updateReq) {
        return receiverBlackListService.updateReceiverBlackList(id, updateReq);
    }

    @ApiOperation("更新黑名单")
    @DeleteMapping("/receiver_black_list/{id}")
    public ReceiverBlackList delReceiverBlackList(@PathVariable("id") Long id) {
        return receiverBlackListService.delReceiverBlackList(id);
    }
}
