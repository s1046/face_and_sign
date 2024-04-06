package com.hbjc.facce.ctrl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.hbjc.facce.model.CompanyModel;
import com.hbjc.facce.model.UserModel;
import com.hbjc.facce.service.MyService;
import com.hbjc.facce.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.*;
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
    public List<String>  queryCompanys(){
        List<UserModel> userModelList;
        String userListStr=cache.getIfPresent("userList");
        if(userListStr!=null){
            userModelList = JSONArray.parseArray(userListStr, UserModel.class);
        }else {
            List<Map<String, Object>> mapList = myService.loadAllUsers();
            userModelList = JSONArray.parseArray(JSONObject.toJSONString(mapList), UserModel.class);
            cache.put("userList",JSONObject.toJSONString(userModelList));
        }
        List<CompanyModel> lists=new ArrayList<>();
        Set<CompanyModel> collect = userModelList.stream().map(item->{
            CompanyModel companyModel=new CompanyModel();
            companyModel.set单位全称(item.get单位全称());
            companyModel.set单位编码(item.get单位编码());
            return companyModel;
        }).collect(Collectors.toSet());
        lists.addAll(collect);
        CompanyModel companyModel=new CompanyModel("湖北省储备粮油管理有限公司","DW0009");
        lists.remove(companyModel);
        Collections.sort(lists, new Comparator<CompanyModel>() {
            @Override
            public int compare(CompanyModel o1, CompanyModel o2) {
                return o1.get单位编码().compareTo(o2.get单位编码());
            }
        });
        lists.add(0,companyModel);

        return lists.stream().map(i->i.get单位全称()).collect(Collectors.toList());
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
//            UserModel userModel=new UserModel();
//            userModel.setId(-999);
//            return userModel;

            UserModel userModel=new UserModel();
            userModel.setId(11111111);
            userModel.set_active(false);
            userModel.setLogin_name("徐兵");
            userModel.set单位全称("哈哈");
            userModel.setUser_name("徐兵");
            return  userModel;

        }
    }


}
