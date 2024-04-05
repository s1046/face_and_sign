package com.hbjc.facce.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class BeanConfig {

    @Bean
    public Cache<String,String> buildCache(){
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS) // 设置缓存条目在1小时内没有被访问后被移除
                .maximumSize(100) // 设置缓存的最大容量为100
                .build();
        return cache;
    }


    @Bean
    public freemarker.template.Configuration  buildConfiguration(){
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(BeanConfig.class, "/debugJson");
        return cfg;
    }

}
