package com.hbjc.facce.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 在线验章响应实体
 *
 * @author huangwb
 */
@Getter
@Setter
public class VerifySignatureResp implements Serializable {
    /**
     * 签名人
     */
    private String cName;
    /**
     * 所在页
     */
    private int pageNum;
    /**
     * 签名时间
     */
    private String signDate;
    /**
     * 域  PDF专属
     */
    private String attribute;

    /**
     * 签章唯一id
     */
    private Integer sigId;
    /**
     * 印章所在区域 x，y,宽，高
     */
    private String[] rect;
    /**
     * 所属机构信息
     */
    private String issOrg;
    /**
     * 验章状态 1:成功 0:失败
     */
    private int verifyState;
    /**
     * 验章说明
     */
    private String verifyMsg;
    /**
     * 证书base64数据
     */
    private String certificate;
}
