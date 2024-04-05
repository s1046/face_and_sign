package com.hbjc.facce.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class UserModel {
    private Integer id;
    private String user_name;
    private String login_name;
    private String 单位全称;
    @JSONField(name = "is_active")
    private boolean is_active;
}
