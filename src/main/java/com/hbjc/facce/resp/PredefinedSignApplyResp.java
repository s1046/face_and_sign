package com.hbjc.facce.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 预定义盖章响应实体
 *
 * @author huangwb
 */
@Getter
@Setter
public class PredefinedSignApplyResp implements Serializable {
    /**
     * 调用方业务id
     */
    private String serialNo;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 预定义盖章url
     */
    private String url;
}
