package com.hbjc.facce.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 下载文件响应实体
 *
 * @author huangwb
 */
@Getter
@Setter
public class DownFileResp implements Serializable {
    /**
     * 文件内容 base64
     */
    private String fileByte;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件格式
     */
    private String fileType;
    /**
     * 文件id
     */
    private String fileId;
    /**
     * 业务id
     */
    private String businessId;
    /**
     * 文件下载返回方式(1,2)
     * 1-下载文件到指定目录
     * 2-下载文件返回Base64
     * 3-返回文件流
     */
    private Integer downType;
}
