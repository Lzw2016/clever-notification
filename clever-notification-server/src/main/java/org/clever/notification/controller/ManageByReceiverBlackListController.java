package org.clever.notification.controller;

import io.swagger.annotations.Api;
import org.clever.notification.service.ReceiverBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
