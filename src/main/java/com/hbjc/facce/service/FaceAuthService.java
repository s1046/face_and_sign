package com.hbjc.facce.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.hbjc.facce.model.ScanFaceResultModel;
import com.hbjc.facce.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FaceAuthService {

    @Autowired
    private Cache<String,String> cache;

    @Autowired
    private MyService myService;


    /**
     * 用户存在时需要 修改isActive
     * @param scanFaceResultModel
     */
    @Async
    public void updateUserWhenPresent(ScanFaceResultModel scanFaceResultModel){
        String biz_id = scanFaceResultModel.getBiz_id();
        Map map = JSON.parseObject(cache.getIfPresent(biz_id),Map.class);
        String idcard_name=map.get("idcard_name").toString();
        String idcard_number=map.get("idcard_number").toString();
        String company=map.get("company").toString();

        String userListStr=cache.getIfPresent("userList");
        List<UserModel> userModelList;
        if(userListStr!=null){
            userModelList = JSONArray.parseArray(userListStr, UserModel.class);
        }else {
            List<Map<String, Object>> mapList = myService.loadAllUsers();
            userModelList = JSONArray.parseArray(JSONObject.toJSONString(mapList), UserModel.class);
            cache.put("userList",JSONObject.toJSONString(userModelList));
        }
        Optional<UserModel> any = userModelList.stream().filter(i -> i.get单位全称().equals(company) && i.getUser_name().equals(idcard_name)).findAny();
        any.ifPresent(userModel -> myService.updateIsActive(userModel.getId(), idcard_number));
    }
}
