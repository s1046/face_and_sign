package com.hbjc.facce.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刷脸认证初始化模型
 */
@Data
@NoArgsConstructor
public class ScanFaceInitModel{
    /**
     * 1.0
     */
    public String version="1.0";
    /**
     * 请求时间戳(毫秒)
     */
    public String timestamp;
    /**
     * 项目授权账号??
     */
    public String account;
    /**
     * 姓名
     */
    public String idcard_name;
    /**
     * 身份证号码
     */
    public String idcard_number;
    /**
     * 回调地址。必须以 http 或者https 开头；刷脸操作完成时将会回调该地址，并携带
     * 业务号参数 biz_id
     */
    public String return_url;

    /**
     * 调用方业务编号。保证唯一且不超过 32 位
     */
    public String query_code;

    /**
     *
     */

    public transient String appKey;

    /**
     * 将以上传入参数外加项目授权密码排序拼接字符串后进
     * 行 md5 签名
     */
    public String sign;

    /**
     * 这个参数主要用来定位用户
     */
    public transient String company;


}
