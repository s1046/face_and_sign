package com.hbjc.facce.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AuthContext implements ApplicationContextAware {

    public static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext=ctx;
    }



}
