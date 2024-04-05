package com.hbjc.facce.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpClientUtilsV2 {
    /**
     * 发送JSON
     *
     * @param path
     * @param post
     * @return
     */
    public static String postJson(String path, String post) {
        byte[] bytes = post(path, post, "application/json", null);
        return new String(bytes, StandardCharsets.UTF_8);
    }
    /**
     * 发送请求
     *
     * @param url
     * @param json
     */
    public static String send(String url, String json, boolean isSign) {
        String result;
        if (isSign) {
            String version = AztUtilsV2.VERSION;
            String appId = AztUtilsV2.APP_ID;
            //13位时间戳
            long time = System.currentTimeMillis();
            //8位随机数
            String nonce = "12345678";
            //JSON测试文件路径
            Map<String, String> heards = new HashMap<>();
            heards.put("appId", appId);
            heards.put("time", String.valueOf(time));
            heards.put("nonce", nonce);
            heards.put("version", version);
            heards.put("sign", AztSignUtilV2.getSign(appId, time, nonce, version, json));
            result = HttpClientUtilsV2.postJson(url, json, heards);
        } else {
            System.out.println("打印信息" + json);
            result = HttpClientUtilsV2.postJson(url, json);
        }
        System.out.println("返回信息：" + result);
        return result;
    }
    /**
     * 发送请求
     *
     * @param url
     * @param json
     */
    public static String send(String url, String json) {
        String version = AztUtilsV2.VERSION;
        String appId = AztUtilsV2.APP_ID;
        //13位时间戳
        long time = System.currentTimeMillis();
        //8位随机数
        String nonce = "12345678";
        //JSON测试文件路径
        Map<String, String> heards = new HashMap<>();
        heards.put("appId", appId);
        heards.put("time", String.valueOf(time));
        heards.put("nonce", nonce);
        heards.put("version", version);
        heards.put("sign", AztSignUtilV2.getSign(appId, time, nonce, version, json));
        String s = HttpClientUtilsV2.postJson(url, json, heards);
        log.info("返回信息：" + s);
        return s;
    }


    public static String send(String url, String json,String v) {
        String version = AztUtilsV2.VERSION;
        String appId = AztUtilsV2.APP_ID;
        //13位时间戳
        long time = System.currentTimeMillis();
        //8位随机数
        String nonce = "12345678";
        //JSON测试文件路径
        Map<String, String> heards = new HashMap<>();
        heards.put("appId", appId);
        heards.put("time", String.valueOf(time));
        heards.put("nonce", nonce);
        heards.put("version", version);
        heards.put("sign", AztSignUtilV2.getSign(appId, time, nonce, version, json));
        String s = HttpClientUtilsV2.postJson(url, json, heards);
        log.info("返回信息：" + s);
        return s;
    }

    public static byte[] download(String url, String json) {
        String version = AztUtils.VERSION;
        String appId = AztUtils.APP_ID;
        //13位时间戳
        long time = System.currentTimeMillis();
        //8位随机数
        String nonce = "12345678";
        //JSON测试文件路径
        Map<String, String> heards = new HashMap<>();
        heards.put("appId", appId);
        heards.put("time", String.valueOf(time));
        heards.put("nonce", nonce);
        heards.put("version", version);
        heards.put("sign", AztSignUtil.getSign(appId, time, nonce, version, json));
        return HttpClientUtilsV2.download(url, json, heards);
    }

    /**
     * 发送JSON带请求头信息
     *
     * @param path
     * @param post
     * @param heards
     * @return
     */
    public static String postJson(String path, String post, Map<String, String> heards) {
        byte[] bytes = post(path, post, "application/json", heards);
        return new String(bytes, StandardCharsets.UTF_8);

    }

    public static byte[] download(String path, String post, Map<String, String> heards) {
        return post(path, post, "application/json", heards);
    }
    /**
     * 发送x-www-form-urlencoded数据
     *
     * @param path
     * @param post
     * @return
     */
    public static String postFormUrlEncode(String path, String post) {
        byte[] bytes = post(path, post, "application/x-www-form-urlencoded", null);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static byte[] post(String path, String post, String contentType, Map<String, String> heards) {
        try {
            HttpURLConnection httpURLConnection = getHttpUrlConnection(path);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.addRequestProperty("Content-Type", contentType);
            if (heards != null) {
                heards.forEach(httpURLConnection::addRequestProperty);
            }
            if (post != null && post.trim().length() > 0) {
                // 获取URLConnection对象对应的输出流
                DataOutputStream printWriter = new DataOutputStream(httpURLConnection.getOutputStream());
                // 发送请求参数
                printWriter.write(post.getBytes("utf-8"));//post的参数 xx=xx&yy=yy
                // flush输出流的缓冲
                printWriter.flush();
            }
            return wrapperResult(path, post, httpURLConnection);
        } catch (IOException e) {
//            log.error("请求转发出现异常,请求地址：{},请求参数：{}", path, post, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String get(String url) {
        String result = null;
        try {
            HttpURLConnection conn = getHttpUrlConnection(url);
            conn.setRequestMethod("GET");
            byte[] bytes = wrapperResult(url, null, conn);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
//            log.error("{}网络请求异常", url, e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取URL连接
     *
     * @param serverUrl
     * @return
     */
    private static HttpURLConnection getHttpUrlConnection(String serverUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(serverUrl);
            conn = (HttpURLConnection) url.openConnection();
            //连接超时 单位毫秒
            conn.setConnectTimeout(100000);
            //读取超时 单位毫秒
            conn.setReadTimeout(30000);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 将 InputStream 转化为 byte[]
     *
     * @param iis 待转化的 InputStream
     * @return 转化后的 byte[]
     */
    public static byte[] transInputStreamToBytes(InputStream iis) {
        byte[] in2b = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[4096];
            int len = 0;
            while ((len = iis.read(buff, 0, 4096)) > 0) {
                baos.write(buff, 0, len);
                baos.flush();
            }
            in2b = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in2b;
    }

    /**
     * 包装结果信息
     *
     * @param url  请求地址
     * @param data 请求数据
     * @param conn 连接信息
     * @return
     * @throws IOException
     */
    private static byte[] wrapperResult(String url, String data, HttpURLConnection conn) throws IOException {
        if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
            try (InputStream iis = conn.getInputStream()) {
                return transInputStreamToBytes(iis);
            }
        } else {
            try (InputStream iis = conn.getErrorStream()) {
                byte[] bytes = transInputStreamToBytes(iis);
                String result = new String(bytes, StandardCharsets.UTF_8);
//                log.error("请求转发出现异常,请求地址：{},请求参数：{},请求响应状态码：{},返回信息：{}", url, data, conn.getResponseCode(), result);
                throw new RuntimeException("请求网络接口出现异常");
            }
        }
    }
}
