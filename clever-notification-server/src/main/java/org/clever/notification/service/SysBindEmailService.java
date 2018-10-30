package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.clever.notification.mapper.SysBindEmailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-30 21:35 <br/>
 */
@Transactional(readOnly = true)
@Service
@Slf4j
public class SysBindEmailService {

    @Autowired
    private SysBindEmailMapper sysBindEmailMapper;


}
