package org.clever.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.MessageTemplateQueryReq;
import org.clever.notification.dto.request.MessageTemplateUpdateReq;
import org.clever.notification.entity.MessageTemplate;
import org.clever.notification.mapper.MessageTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 11:12 <br/>
 */
@Service
@Slf4j
public class MessageTemplateService {

    @Autowired
    private Configuration configuration;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisStringTemplateLoader redisStringTemplateLoader;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    /**
     * 所有的模版名称集合 - key前缀
     */
    private static final String NamesSetKey = RedisStringTemplateLoader.KeyPrefix + ":names:set";

    /**
     * 所有的模版名称集合 - key前缀 临时
     */
    private static final String NamesSetKeyTmp = RedisStringTemplateLoader.KeyPrefix + ":names:set-tmp";

    @Transactional
    @PostConstruct
    protected void init() {
        // configuration.unsetLocale();
        configuration.setTemplateLoader(redisStringTemplateLoader);
        load();
    }

    /**
     * 加载数据库中所有的消息模版
     */
    @SuppressWarnings({"Duplicates", "ConstantConditions"})
    public synchronized void load() {
        // 初始化 NamesSetKey NamesSetKeyTmp
        if (!redisTemplate.hasKey(NamesSetKey)) {
            Set<String> set = redisTemplate.keys(NamesSetKey + ":*");
            if (set.size() <= 0) {
                set.add("");
            }
            redisTemplate.opsForSet().add(NamesSetKey, set.toArray(new String[]{}));
        }
        if (!redisTemplate.hasKey(NamesSetKeyTmp)) {
            redisTemplate.opsForSet().add(NamesSetKeyTmp, "");
        }
        // 查询所有的消息模版
        List<MessageTemplate> messageTemplateList = messageTemplateMapper.findAllEnabled();
        // 删除当前不存在的数据
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (MessageTemplate messageTemplate : messageTemplateList) {
                connection.sAdd(NamesSetKeyTmp.getBytes(), messageTemplate.getName().getBytes());
            }
            return null;
        });
        // 求差集
        Set<String> keySet = redisTemplate.opsForSet().difference(NamesSetKey, NamesSetKeyTmp);
        redisStringTemplateLoader.removeTemplate(keySet);
        // 替换 NamesSetKeyTmp -> NamesSetKey
        redisTemplate.delete(NamesSetKey);
        redisTemplate.rename(NamesSetKeyTmp, NamesSetKey);
        // 更新
        for (MessageTemplate messageTemplate : messageTemplateList) {
            putTemplate(messageTemplate);
        }
        log.info("### 加载所有消息模版, 新增/更新: {} -> 删除:{}", messageTemplateList.size(), keySet.size());
    }

    private synchronized void putTemplate(MessageTemplate messageTemplate) {
        Date lastModified = messageTemplate.getUpdateAt();
        if (lastModified == null) {
            lastModified = messageTemplate.getCreateAt();
        }
        if (lastModified == null) {
            lastModified = new Date();
        }
        redisStringTemplateLoader.putTemplate(messageTemplate.getName(), messageTemplate.getContent(), lastModified.getTime());
    }

    /**
     * 判断消息模版是否存在
     */
    public boolean templateExists(String templateName) {
        try {
            return configuration.getTemplate(templateName) != null;
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }


    public IPage<MessageTemplate> findByPage(MessageTemplateQueryReq query) {
        Page<MessageTemplate> page = new Page<>(query.getPageNo(), query.getPageSize());
        page.setRecords(messageTemplateMapper.findByPage(query, page));
        return page;
    }

    @Transactional
    public MessageTemplate addMessageTemplate(MessageTemplate messageTemplate) {
        int count = messageTemplateMapper.exists(messageTemplate.getName());
        if (count >= 1) {
            throw new BusinessException("消息模版已经存在");
        }
        messageTemplateMapper.insert(messageTemplate);
        messageTemplate = messageTemplateMapper.selectById(messageTemplate.getId());
        putTemplate(messageTemplate);
        return messageTemplate;
    }

    @Transactional
    public MessageTemplate updateMessageTemplate(Long id, MessageTemplateUpdateReq updateReq) {
        MessageTemplate oldMessageTemplate = messageTemplateMapper.selectById(id);
        if (oldMessageTemplate == null) {
            throw new BusinessException("更新数据不存在");
        }
        // 校验模版名称唯一
        if (StringUtils.isNotBlank(updateReq.getName()) && !Objects.equals(updateReq.getName(), oldMessageTemplate.getName())) {
            MessageTemplate tmp = messageTemplateMapper.getByName(updateReq.getName());
            if (tmp != null && !Objects.equals(oldMessageTemplate.getId(), tmp.getId())) {
                throw new BusinessException("模版名称已经存在");
            }
        }
        // 更新数据
        MessageTemplate newMessageTemplate = BeanMapper.mapper(updateReq, MessageTemplate.class);
        newMessageTemplate.setId(oldMessageTemplate.getId());
        messageTemplateMapper.updateById(newMessageTemplate);
        newMessageTemplate = messageTemplateMapper.selectById(newMessageTemplate.getId());
        // 更新模版缓存
        if (Objects.equals(oldMessageTemplate.getName(), newMessageTemplate.getName())) {
            putTemplate(newMessageTemplate);
        } else {
            putTemplate(newMessageTemplate);
            redisStringTemplateLoader.removeTemplate(oldMessageTemplate.getName());
        }
        return newMessageTemplate;
    }

    @Transactional
    public MessageTemplate delMessageTemplate(Long id) {
        MessageTemplate messageTemplate = messageTemplateMapper.selectById(id);
        if (messageTemplate == null) {
            throw new BusinessException("删除数据不存在");
        }
        redisStringTemplateLoader.removeTemplate(messageTemplate.getName());
        return messageTemplate;
    }
}
