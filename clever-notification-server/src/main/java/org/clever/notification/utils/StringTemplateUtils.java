package org.clever.notification.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.spring.SpringContextHolder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 9:53 <br/>
 */
public class StringTemplateUtils {

    private static final Configuration configuration;

    static {
        configuration = SpringContextHolder.getBean(Configuration.class);
    }

    /**
     * 根据字符串模板和模型数据生成字符串内容
     *
     * @param strTemplate 字符串模版
     * @param model       模型数据
     */
    public static String getContentByStrTemplate(String strTemplate, Object model) {
        try {
            Template template = new Template("MessageTemplate-Tmp", strTemplate, configuration);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 根据字符串模板和模型数据生成字符串内容
     *
     * @param templateName 模板名称
     * @param model        模型数据
     */
    public static String getContentByTemplateName(String templateName, Object model) {
        try {
            Template template = configuration.getTemplate(templateName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }
}
