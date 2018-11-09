package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
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
}
