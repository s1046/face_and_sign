package com.hbjc.facce.ctrl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.hbjc.facce.model.UserModel;
import com.hbjc.facce.service.MyService;
import com.hbjc.facce.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class QueryUserCtrl {

    @Autowired
    private MyService myService;

    @Autowired
    private Cache<String,String> cache;



    /**
     * 返回所有的公司名称
     * @return
     */
    @GetMapping("/queryCompanys")
    @CrossOrigin
    public Set  queryCompanys(){
        List<UserModel> userModelList;
        String userListStr=cache.getIfPresent("userList");
        if(userListStr!=null){
            userModelList = JSONArray.parseArray(userListStr, UserModel.class);
        }else {
            List<Map<String, Object>> mapList = myService.loadAllUsers();
            userModelList = JSONArray.parseArray(JSONObject.toJSONString(mapList), UserModel.class);
            cache.put("userList",JSONObject.toJSONString(userModelList));
        }
        Set<String> collect = userModelList.stream().map(UserModel::get单位全称).collect(Collectors.toSet());
        return collect;
    }

    /**
     * 查询用户是否是isActive，如果查询不到的时候，构造一个数据返回
     * @param params
     * @return
     */
    @PostMapping("/queryUserActive")
    @CrossOrigin
    public UserModel  queryAllUsers(@RequestBody Map params){
        List<UserModel> userModelList;
        String userListStr=cache.getIfPresent("userList");
        if(userListStr!=null){
            userModelList = JSONArray.parseArray(userListStr, UserModel.class);
        }else {
            List<Map<String, Object>> mapList = myService.loadAllUsers();
            userModelList = JSONArray.parseArray(JSONObject.toJSONString(mapList), UserModel.class);
            cache.put("userList",JSONObject.toJSONString(userModelList));
        }
        String user_name=params.get("user_name").toString();
        String dept_name=params.get("dept_name").toString();
        String idcard_no=params.get("idcard_no").toString();
        Optional<UserModel> optionalUserModel = userModelList.stream().filter(i -> i.getUser_name().equals(user_name) && i.get单位全称().equals(dept_name)).findAny();
        if(optionalUserModel.isPresent()){
            return optionalUserModel.get();
        }else {
            //查不到的时候构造一个
            UserModel userModel=new UserModel();
            userModel.setId(-999);
            return userModel;
        }
    }


}
