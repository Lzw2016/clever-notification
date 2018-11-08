package org.clever.notification.service;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-07 16:23 <br/>
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
@Component
@Slf4j
public class RedisStringTemplateLoader implements TemplateLoader {

    /**
     * key前缀
     */
    private static final String KeyPrefix = "clever-notification:message-template";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private Configuration configuration;

    public void putTemplate(String name, String templateContent) {
        putTemplate(name, templateContent, System.currentTimeMillis());
    }

    public void putTemplate(String name, String templateContent, long lastModified) {
        redisTemplate.opsForValue().set(
                String.format("%s:%s", KeyPrefix, name),
                new RedisStringTemplateLoader.StringTemplateSource(name, templateContent, lastModified)
        );
    }

    public boolean removeTemplate(String name) {
        Boolean result = redisTemplate.delete(String.format("%s:%s", KeyPrefix, name));
        return result != null && result;
    }

    @Override
    public Object findTemplateSource(String name) {
        // 除去模板名称后缀
        Locale templateLocale = configuration.getLocale();
        if (templateLocale != null) {
            String suffix = "_" + templateLocale.toString();
            while (true) {
                if (name.endsWith(suffix)) {
                    name = name.substring(0, name.length() - suffix.length());
                    break;
                }
                int lastUnderscore = suffix.lastIndexOf('_');
                if (lastUnderscore < 0) {
                    break;
                }
                suffix = suffix.substring(0, lastUnderscore);
            }
        }
        // 获取模板
        String key = String.format("%s:%s", KeyPrefix, name);
        log.debug("### 从Redis读取模板模板 -> {}", key);
        Object object = redisTemplate.opsForValue().get(key);
        if (object == null) {
            log.info("### 模板不存在 -> {}", key);
        }
        return object;
    }

    @Override
    public long getLastModified(Object templateSource) {
        return ((RedisStringTemplateLoader.StringTemplateSource) templateSource).lastModified;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) {
        return new StringReader(((RedisStringTemplateLoader.StringTemplateSource) templateSource).templateContent);
    }

    @Override
    public void closeTemplateSource(Object templateSource) {
//        Set<Object> set = redisTemplate.keys(KeyPrefix + ":*");
//        if (set != null && set.size() > 0) {
//            redisTemplate.delete(set);
//        }
    }

    @Getter
    @Setter
    private static class StringTemplateSource {
        private String name;
        private String templateContent;
        private long lastModified;

        public StringTemplateSource() {

        }

        StringTemplateSource(String name, String templateContent, long lastModified) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }
            if (templateContent == null) {
                throw new IllegalArgumentException("source == null");
            }
            if (lastModified < -1L) {
                throw new IllegalArgumentException("lastModified < -1L");
            }
            this.name = name;
            this.templateContent = templateContent;
            this.lastModified = lastModified;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + name.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RedisStringTemplateLoader.StringTemplateSource other = (RedisStringTemplateLoader.StringTemplateSource) obj;
            return name.equals(other.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
