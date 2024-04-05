package com.hbjc.facce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/h5/").setViewName("forward:/h5/index.html");
        WebMvcConfigurer.super.addViewControllers(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //1、添加资源处理器路径 即每次访问静态资源都得添加"/static/",例如localhost:8080/static/j1.jpg
        //若registry.addResourceHandler("/s/**") 则必须访问localhost:8080/s/j1.jpg
        registry.addResourceHandler("/static/**", "/static/h5/**")
                //2、添加了资源处理器路径后对应的映射资源路径
                .addResourceLocations("classpath:/static/", "classpath:/static/h5");
    }
}
