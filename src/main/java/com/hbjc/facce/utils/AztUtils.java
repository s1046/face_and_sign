package com.hbjc.facce.utils;

import com.alibaba.fastjson.JSONObject;
import com.hbjc.facce.service.AuthContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * 工具常量类
 */
public class AztUtils {
    /**
     * 云文档提供的APP_ID
     */
    public static String APP_ID;
    /**
     * 云文档提供的APP_SECRET
     */
    public static String APP_SECRET;
    /**
     * 接口调用前缀地址 例如:http://ip:port/iCloudDoc
     */
    public static String ICLOUD_DOC_PREFIX;
    /**
     * 回调调试地址
     */
    public static String NOTIFY_URL;
    /**
     * 云文档版本地址 默认1.0
     */
    public static String VERSION;
    /**
     * JSON文件地址 如果将JSON文件拷贝出来请配置 例如静默签场景：jsonFilePath=D://test// 就会去加载D盘下面的D://test//debugJson//request//静默签//企业静默签.json5
     */
    public static String JSON_FILE_PATH;

    static {
            Environment environment=AuthContext.applicationContext.getBean(Environment.class);
            ICLOUD_DOC_PREFIX = environment.getProperty("azt.v1.aztUrl");
            APP_ID = environment.getProperty("azt.v1.appId");
            APP_SECRET = environment.getProperty("azt.v1.appSecret");
            NOTIFY_URL = environment.getProperty("azt.v1.notifyUrl");
            VERSION = environment.getProperty("azt.v1.version");
            JSON_FILE_PATH = environment.getProperty("azt.v1.jsonFilePath");
            if (StringUtils.isEmpty(JSON_FILE_PATH)) {
                JSON_FILE_PATH = "";
            }
    }

    public static String base64Escape(String base64) {
        return base64.replaceAll("\\+", "%2B");
    }


    public static String readDebugJson(String relativePath) throws URISyntaxException, IOException {
        if (StringUtils.isEmpty(JSON_FILE_PATH)) {
            System.out.println("加载文件路径" + relativePath);
            return FileUtils.readFileToString(new File(AztUtils.class.getClassLoader().getResource(relativePath).toURI().getPath()));
        }
        System.out.println("加载文件路径" + JSON_FILE_PATH + relativePath);
        return FileUtils.readFileToString(Paths.get(JSON_FILE_PATH + relativePath).toFile());
    }
}
