package com.hbjc.facce;


import com.hbjc.facce.service.MyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Slf4j
public class MyServiceTest {

    @Autowired
    private MyService myService;

    @Test
    public void loadTemplate(){
        Map<String, Object> stringObjectMap = myService.loadTemplateConfig("235");

        System.out.println(stringObjectMap);
    }
}
