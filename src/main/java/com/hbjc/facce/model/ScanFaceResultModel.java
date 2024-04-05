package com.hbjc.facce.model;

import lombok.Data;

/**
 * 查询人脸识别的结果
 */
@Data
public class ScanFaceResultModel {
    public String version="1.0";
    public String timestamp;
    public String account;
    public String biz_id;
    public  String sign;
    public  transient String appKey;
}
