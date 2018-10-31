package org.clever.notification.service;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.entity.MessageTemplate;
import org.clever.notification.mapper.MessageTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 11:12 <br/>
 */
@Transactional
@Service
@Slf4j
public class MessageTemplateService {

    @Autowired
    private Configuration configuration;
    @Autowired
    private StringTemplateLoader stringTemplateLoader;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;

    @PostConstruct
    private void init() {
        configuration.setTemplateLoader(stringTemplateLoader);
        List<MessageTemplate> messageTemplateList = messageTemplateMapper.getAllEnabled();
        for (MessageTemplate messageTemplate : messageTemplateList) {
            Date lastModified = messageTemplate.getUpdateAt();
            if (lastModified == null) {
                lastModified = messageTemplate.getCreateAt();
            }
            if (lastModified == null) {
                lastModified = new Date();
            }
            stringTemplateLoader.putTemplate(messageTemplate.getName(), messageTemplate.getContent(), lastModified.getTime());
        }
        log.info("### 加载所有消息模版, 数量: {}", messageTemplateList.size());
        // TODO 集群修改刷新？
    }

    public boolean templateExists(String templateName) {
        return stringTemplateLoader.findTemplateSource(templateName) != null;
    }
}
