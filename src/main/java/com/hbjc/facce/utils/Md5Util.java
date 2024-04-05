package com.hbjc.facce.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
 
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
 
public class Md5Util {
 
    private static MessageDigest md5;
 
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    public static String getMd5(String string) {
        try {
            byte[] bs = md5.digest(string.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(40);
            for (byte x : bs) {
                if ((x & 0xff) >> 4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 0xff));
                } else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMD5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            return DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 
    public static void main(String[] args) throws Exception {
        System.out.println(getMd5("HelloWorld"));
    }
}