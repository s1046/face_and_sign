package com.hbjc.facce.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 安证通演示demo签名工具类
 */
@Slf4j
public class AztSignUtil {
    /**
     * @param secretKey 签名密钥
     * @param params    JSON参数字符串
     * @return
     */
    public static String getSign(final String secretKey, final String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        Map<String, Object> newParams = getSortJson(jsonObject);
        return getSign(secretKey, newParams);
    }

    /**
     * 签名计算
     *
     * @param secretKey 签名密钥
     * @param param     参数
     * @return
     */
    public static String getSign(final String secretKey, final Map<String, Object> param) {
        Map<String, Object> newParams = param;
        //按照key进行排序
        String[] keys = new String[newParams.size()];
        System.arraycopy(newParams.keySet().toArray(), 0, keys, 0, keys.length);
        Arrays.sort(keys);
        //组装数据
        List<String> paramsPairs = new ArrayList<String>(keys.length);
        for (String key : keys) {
            paramsPairs.add(key + "=" + newParams.get(key));
        }
        String paramsStr = StringUtils.join( paramsPairs,"&");
        String rawStr = paramsStr + "&" + secretKey;
        System.out.println("签名计算明文信息：" + rawStr);
        return getSha256Encoding(rawStr);
    }

    /**
     * JSON内容按照Key排序排序
     */
    /**
     * 排序
     */
    public static JSONObject getSortJson(JSONObject json) {
        if (Objects.isNull(json)) {
            return new JSONObject();
        }
        Set<String> keySet = json.keySet();
        SortedMap<String, Object> map = new TreeMap<>();
        for (String key : keySet) {
            Object value = json.get(key);
            if (Objects.nonNull(value) && value instanceof JSONArray) {
                JSONArray array = json.getJSONArray(key);
                JSONArray jsonArray = new JSONArray(new LinkedList<>());
                for (int i = 0; i < array.size(); i++) {
                    JSONObject sortJson = getSortJson(array.getJSONObject(i));
                    jsonArray.add(sortJson);
                }
                map.put(key, jsonArray);
            } else if (Objects.nonNull(value) && value instanceof JSONObject) {
                JSONObject sortJson = getSortJson(json.getJSONObject(key));
                map.put(key, sortJson);
            } else {
                map.put(key, value);
            }
        }
        return new JSONObject(map);
    }

    /**
     * sha256加密
     *
     * @param str 要加密的字符串
     * @return 加密后的字符串
     */
    private static String getSha256Encoding(final String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * sha256加密 将byte转为16进制
     *
     * @param bytes 字节码
     * @return 加密后的字符串
     */
    private static String byte2Hex(final byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

    public static String getSign(String appId, long time, String nonce, String version, String json) {
        Map<String, Object> params = JSONObject.parseObject(json);
        params.put("appId", appId);
        params.put("time", time);
        params.put("nonce", nonce);
        params.put("version", version);
        params.remove("fileContent");
        return AztSignUtil.getSign(AztUtils.APP_SECRET, JSON.toJSONString(params));
    }
}
