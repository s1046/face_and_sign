package com.hbjc.facce.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件上传响应实体
 *
 * @author huangwb
 */
@Getter
@Setter
public class UploadFileResp implements Serializable {
    /**
     * 云文档返回的文件唯一编号
     */
    private String fileId;
    /**
     * 文档总页数
     */
    private Integer pageCount;
    /**
     * 文件格式
     */
    private String format;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 业务id
     */
    private String businessId;
    /**
     * 外部存储业务FileKey
     */
    private String fileKey;
}
