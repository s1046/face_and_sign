package com.hbjc.facce.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 签章申请响应实体
 *
 * @author huangwb
 */
@Getter
@Setter
public class SignApplyResp implements Serializable {
    /**
     * 业务id
     */
    private String businessId;
    /**
     * 签章URL
     */
    private String url;
    /**
     * 签章申请用户传递业务号
     */
    private String serialNo;

    /**
     * 签署文件信息
     */
    private List<SignApplyFile> signApplyFiles = new ArrayList<>();

    @Getter
    @Setter
    public static class SignApplyFile implements Serializable {
        /**
         * 文件id
         */
        private String fileId;
        /**
         * 文件外部存储关键key
         */
        private String fileKey;
        /**
         * 文件超时时间
         */
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private Date delTime;

        /**
         * 静默签同步状态下 返回文件状态
         */
        private Integer state;

        /**
         * 静默签同步状态下 错误说明
         */
        private String errorMsg;
    }
}
