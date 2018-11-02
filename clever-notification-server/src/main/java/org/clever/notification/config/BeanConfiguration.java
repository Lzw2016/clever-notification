package org.clever.notification.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.cache.StringTemplateLoader;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.SnowFlake;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 作者： lzw<br/>
 * 创建时间：2017-12-04 10:37 <br/>
 */
@Configuration
@Slf4j
public class BeanConfiguration {

    @Bean
    protected MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "clever-notification-server");
    }

    /**
     * 分页插件
     */
    @Bean
    protected PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        paginationInterceptor.setSqlParser()
//        paginationInterceptor.setDialectClazz()
        paginationInterceptor.setOverflow(false);
//        paginationInterceptor.setProperties();
        return paginationInterceptor;
    }

    /**
     * 乐观锁插件<br />
     * 取出记录时，获取当前version <br />
     * 更新时，带上这个version <br />
     * 执行更新时， set version = yourVersion+1 where version = yourVersion <br />
     * 如果version不对，就更新失败 <br />
     */
    @Bean
    protected OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

//    /**
//     * 逻辑删除<br />
//     */
//    @Bean
//    public ISqlInjector sqlInjector() {
//        return new LogicSqlInjector();
//    }

    /**
     * SQL执行效率插件
     */
    @Bean
    @Profile({"dev", "test"})
    protected PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setFormat(false);
        performanceInterceptor.setWriteInLog(false);
        performanceInterceptor.setMaxTime(1000);
        return performanceInterceptor;
    }

    /**
     * 执行分析插件<br />
     * SQL 执行分析拦截器【 目前只支持 MYSQL-5.6.3 以上版本 】
     * 作用是分析 处理 DELETE UPDATE 语句
     * 防止小白或者恶意 delete update 全表操作！
     */
    @Bean
    @Profile({"dev", "test"})
    protected SqlExplainInterceptor sqlExplainInterceptor() {
        SqlExplainInterceptor sqlExplainInterceptor = new SqlExplainInterceptor();
//        sqlExplainInterceptor.stopProceed
        return sqlExplainInterceptor;
    }

    /**
     * 雪花算法实例
     */
    @Bean
    protected SnowFlake snowFlake(GlobalConfig globalConfig) {
        GlobalConfig.SnowFlakeConfig snowFlakeConfig = globalConfig.getSnowFlakeConfig();
        log.info("### 雪花算法配置 {}", snowFlakeConfig);
        return new SnowFlake(snowFlakeConfig.getDataCenterId(), snowFlakeConfig.getMachineId());
    }

    @Bean
    protected Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    protected StringTemplateLoader stringTemplateLoader() {
        return new StringTemplateLoader();
    }
}
