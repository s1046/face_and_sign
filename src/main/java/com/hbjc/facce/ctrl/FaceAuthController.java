package com.hbjc.facce.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.hbjc.facce.model.ScanFaceInitModel;
import com.hbjc.facce.model.ScanFaceResultModel;
import com.hbjc.facce.model.UserModel;
import com.hbjc.facce.service.FaceAuthService;
import com.hbjc.facce.service.MyService;
import com.hbjc.facce.util.HttpUtil;
import com.hbjc.facce.util.SignBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
public class FaceAuthController {

    @Value("${baseUrl}")
    private String baseUrl;
    @Value("${appId}")
    private String appId;
    @Value("${appKey}")
    private String appKey;
    @Value("${returnUrl}")
    private String returnUrl;

    @Value("${azt.faceInit}")
    private String faceInit;

    @Value("${azt.faceResult}")
    private String faceResult;

   @Autowired
   private Cache<String,String> cache;

  @Autowired
  private FaceAuthService faceAuthService;

    /**
     * 刷脸初始化
     * @return
     */
    @CrossOrigin
    @PostMapping("/auth_init")
    public Object authInit(@RequestBody ScanFaceInitModel scanFaceInitModel) throws IllegalAccessException {
        String timestamp=String.valueOf(System.currentTimeMillis());
        scanFaceInitModel.setTimestamp(timestamp);
        scanFaceInitModel.setAccount(appId);
        scanFaceInitModel.setAppKey(appKey);
        scanFaceInitModel.setReturn_url(returnUrl);
        scanFaceInitModel.setQuery_code(UUID.randomUUID().toString().replace("-",""));
        scanFaceInitModel.setSign(SignBuilder.buildSign(scanFaceInitModel));
        String faceInitUrl=baseUrl+faceInit;
        log.info("scanFaceInitModel:{}",JSONObject.toJSONString(scanFaceInitModel));
        JSONObject jsonObject = HttpUtil.doPostRequest(faceInitUrl, JSONObject.parseObject(JSONObject.toJSONString(scanFaceInitModel), JSONObject.class),new JSONObject());
        //调用接口认证成功的时候 设置下缓存信息
        if(jsonObject.get("code").equals("0")){
            Map<String,String> map=new HashMap<>();
            map.put("idcard_name",scanFaceInitModel.getIdcard_name());
            map.put("company",scanFaceInitModel.getCompany());
            cache.put(jsonObject.getJSONObject("data").getString("biz_id"),JSONObject.toJSONString(map));

        }
        log.info("jsonObject:{}",jsonObject);
        return  jsonObject;
    }


    /**
     * 认证后的回调地址
     * @return
     */
    @CrossOrigin
    @PostMapping("/auth_call_back")
    public Object authCallBack(@RequestBody ScanFaceResultModel scanFaceResultModel) throws IllegalAccessException {
        String timestamp=String.valueOf(System.currentTimeMillis());
        scanFaceResultModel.setTimestamp(timestamp);
        scanFaceResultModel.setAccount(appId);
        scanFaceResultModel.setAppKey(appKey);
        scanFaceResultModel.setSign(SignBuilder.buildSign(scanFaceResultModel));
        String faceResultUrl=baseUrl+faceResult;
        JSONObject jsonObject = HttpUtil.doPostRequest(faceResultUrl, JSONObject.parseObject(JSONObject.toJSONString(scanFaceResultModel), JSONObject.class),new JSONObject());
        log.info("jsonObject:{}",jsonObject);
        //当认证成功的时候需要设置下isActive
        if(jsonObject.get("code").equals("0")){
            faceAuthService.updateUserWhenPresent(scanFaceResultModel);
        }
        return  jsonObject;
        //return  "{\"code\":\"0\",\"data\":{\"code\":\"0\",\"message\":\"认证通过\",\"time_used\":0},\"message\":\"认证通过\"}";
    }


}
