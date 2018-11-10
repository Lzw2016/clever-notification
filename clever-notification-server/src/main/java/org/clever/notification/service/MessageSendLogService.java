package org.clever.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.dto.request.MessageSendLogQueryReq;
import org.clever.notification.entity.MessageSendLog;
import org.clever.notification.mapper.MessageSendLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-09 23:13 <br/>
 */
@Transactional(readOnly = true)
@Service
@Slf4j
public class MessageSendLogService {

    @Autowired
    private MessageSendLogMapper messageSendLogMapper;

    @Transactional
    public void addMessageSendLog(MessageSendLog messageSendLog) {
        messageSendLogMapper.insert(messageSendLog);
    }

    @Transactional
    public void updateMessageSendLog(MessageSendLog messageSendLog) {
        messageSendLogMapper.updateById(messageSendLog);
    }

    public IPage<MessageSendLog> findByPage(MessageSendLogQueryReq queryReq) {
        Page<MessageSendLog> page = new Page<>(queryReq.getPageNo(), queryReq.getPageSize());
        page.setRecords(messageSendLogMapper.findByPage(queryReq, page));
        return page;
    }
}
