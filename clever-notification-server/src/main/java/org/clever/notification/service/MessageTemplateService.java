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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    // TODO 使用Redis替换
    @Autowired
    private StringTemplateLoader stringTemplateLoader;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    /**
     * 所有的模版名称集合
     */
    private Set<String> oldTemplateNames = new HashSet<>();

    @PostConstruct
    private void init() {
        configuration.setTemplateLoader(stringTemplateLoader);
        load();
    }

    /**
     * 加载数据库中所有的消息模版
     */
    public synchronized void load() {
        List<MessageTemplate> messageTemplateList = messageTemplateMapper.findAllEnabled();
        Set<String> newTemplateNames = new HashSet<>();
        messageTemplateList.forEach(messageTemplate -> newTemplateNames.add(messageTemplate.getName()));
        // 计算要删除的模版 - 以前有现在没有
        Set<String> delTemplateNames = new HashSet<>(oldTemplateNames);
        delTemplateNames.removeAll(newTemplateNames);
        // 删除
        for (String name : delTemplateNames) {
            stringTemplateLoader.removeTemplate(name);
        }
        // 更新
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
        oldTemplateNames = newTemplateNames;
        log.info("### 加载所有消息模版, 新增/更新: {} -> 删除:{}", messageTemplateList.size(), delTemplateNames.size());
    }

    /**
     * 判断消息模版是否存在
     */
    public boolean templateExists(String templateName) {
        return stringTemplateLoader.findTemplateSource(templateName) != null;
    }
}
