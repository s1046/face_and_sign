package com.hbjc.facce.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 签章申请结果响应实体
 *
 * @author huangwb
 */
@Getter
@Setter
public class SignResultResp implements Serializable {
    /**
     * 签章申请业务编号
     */
    private String businessId;
    /**
     * 签章申请调用方流水号
     */
    private String serialNo;
    /**
     * 签署完成状态 -1:签章失败 0:未签章 1:签章中 2:签章完成
     */
    private Integer signCompleteState;
    /**
     * 签署完成时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date signCompleteTime;

    /**
     * 签署申请过期状态 1:过期 0:有效
     */
    private String delState;
    /**
     * 签署申请过期时间 yyyy-MM-dd HH:mm:ss
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date delTime;
    /**
     * 拒签说明 signComplateState=-2时必填
     */
    private String refundReason;
    /**
     * 签章文件明细信息
     */
    private List<SignResultFile> signFiles = new ArrayList<>();

    @Getter
    @Setter
    public static class SignResultFile implements Serializable {
        /**
         * 文件id
         */
        private String fileId;
        /**
         * 文件名称
         */
        private String fileName;
        /**
         * 文件类型 1:普通文件(默认) 2:水印图片 5:需盖章附件 6:无需盖章附件
         */
        private Integer fileType;
        /**
         * 0:未签章 1:签署中 2:签章完成 -1:签署失败
         */
        private Integer fileSignState;
        /**
         * 签章文件时间
         */
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private Date signTime;
    }
}
