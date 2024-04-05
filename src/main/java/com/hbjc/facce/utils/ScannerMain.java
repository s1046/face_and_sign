package com.hbjc.facce.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

@Slf4j
@SuppressWarnings("all")
public class ScannerMain {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("如需修改访问地址、appId、appSecret、notifyUrl配置，请用压缩方式打开更改config.json文件");
        System.out.println("如需修改请求接口的JSON内容，请用压缩方式打开更改debugJson/request下的对应接口JSON文件");
        System.out.println("当前接口访问地址=" + AztUtils.ICLOUD_DOC_PREFIX);
        while (true) {
            System.out.println("请输入你需要测试的模式 1:文件上传 2:签章申请 3:获取签章结果 4:文件下载 5:文件预览 6:在线验章 7:保存签署回调 8:拒绝签署回调 9:完成签署回调 10:预定义盖章申请 11:预定义盖章回调 0:退出，输入完成请回车 ");
            int step = scanner.nextInt();
            if (step == 0) {
                System.out.println("退出成功");
                return;
            }
            String jsonPath = null;
            String url = null;
            boolean isSign = true;
            switch (step) {
                case 1: {
                    jsonPath = "debugJson/request/PDF文件上传.json5";
                    url = AztUtils.ICLOUD_DOC_PREFIX + "/file/v1/upload";
                    break;
                }
                case 2: {
                    jsonPath = "debugJson/request/签章申请.json5";
                    url = AztUtils.ICLOUD_DOC_PREFIX + "/apply/v1/signApply";
                    break;
                }
                case 3: {
                    jsonPath = "debugJson/request/签章申请结果查询.json5";
                    url = AztUtils.ICLOUD_DOC_PREFIX + "/apply/v1/getSignResult";
                    break;
                }
                case 4: {
                    jsonPath = "debugJson/request/文件下载.json5";
                    url = AztUtils.ICLOUD_DOC_PREFIX + "/file/v1/download";
                    break;
                }
                case 5: {
                    jsonPath = "debugJson/request/文件预览.json5";
                    url = AztUtils.ICLOUD_DOC_PREFIX + "/file/v1/filePreview";
                    break;
                }
                case 6: {
                    jsonPath = "debugJson/request/在线验章.json5";
                    url = AztUtils.ICLOUD_DOC_PREFIX + "/file/v1/verifySignature";
                    break;
                }
                case 7: {
                    jsonPath = "debugJson/request/保存签署回调.json5";
                    url = AztUtils.NOTIFY_URL;
                    isSign = false;
                    break;
                }
                case 8: {
                    jsonPath = "debugJson/request/拒绝签署回调.json5";
                    isSign = false;
                    url = AztUtils.NOTIFY_URL;
                    break;
                }
                case 9: {
                    jsonPath = "debugJson/request/完成签署回调.json5";
                    isSign = false;
                    url = AztUtils.NOTIFY_URL;
                    break;
                }
                case 10: {
                    jsonPath = "debugJson/request/预定义盖章申请.json5";
                    url = AztUtils.ICLOUD_DOC_PREFIX + "/predefine/v1/predefinedSignApply";
                    break;
                }
                case 11: {
                    jsonPath = "debugJson/request/预定义盖章完成回调.json5";
                    isSign = false;
                    url = AztUtils.NOTIFY_URL;
                    break;
                }
                case 12: {
                    jsonPath = "debugJson/request/静默签/企业静默签.json5";
                    url = AztUtils.NOTIFY_URL;
                    break;
                }
                default: {
                    System.out.println("输入不正确，请重新操作");
                }
            }
            if (StringUtils.isNotBlank(jsonPath) && StringUtils.isNotBlank(url)) {
                String json = null;
                if (StringUtils.isEmpty(AztUtils.JSON_FILE_PATH)) {
                    InputStream bufferedInputStream = ClassLoader.getSystemResourceAsStream(jsonPath);
                    json = IOUtils.toString(bufferedInputStream, StandardCharsets.UTF_8);
                } else {
                    json = FileUtils.readFileToString(Paths.get(AztUtils.JSON_FILE_PATH + jsonPath).toFile());
                    jsonPath = AztUtils.JSON_FILE_PATH + jsonPath;
                }
                System.out.println("请求接口地址：" + url + "，访问接口JSON文件地址：" + jsonPath);
                String send = HttpClientUtils.send(url, json, isSign);
            }
        }
    }
}
