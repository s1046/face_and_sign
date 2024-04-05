package com.hbjc.facce.utils;

import com.alibaba.fastjson.util.IOUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

/**
 * @author 1921494057@qq.com
 * @date 2019/1/24 15:16
 * @description
 */
public class CommonUtil {
    private static String DEFAULT_ENCODING = "UTF-8";

    /**
     * 将 InputStream 转化为 byte[]
     *
     * @param iis 待转化的 InputStream
     * @return 转化后的 byte[]
     */
    public static byte[] transInputStreamToBytes(InputStream iis) {
        byte[] in2b = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
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

    public static byte[] readBytesFromFile(String fileName) {
        return readBytesFromFile(FileUtils.getFile(fileName));
    }

    public static byte[] readBytesFromFile(File file) {
        byte[] bytes = null;
        try (InputStream iis = new FileInputStream(file);) {
            bytes = transInputStreamToBytes(iis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }


    public static void writeBytesToFile(byte[] bytes, String outputFile) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(outputFile);
            os.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(os);
        }
    }

    public static File getFileFromBytes(byte[] b, String outFile) {
        File file = new File(outFile);

        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(b);
            bos.flush();
            bos.close();
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(bos);
        }
        return file;
    }

    public static String replaceString(String source, String oldStr, String newStr) {
        if (source == null || source.length() < 1) {
            return "";
        }

        String instr = source;
        StringBuilder returnValue = new StringBuilder();
        int pos;
        for (; instr.lastIndexOf(oldStr) >= 0; instr = instr.substring(pos + oldStr.length())) {
            pos = instr.lastIndexOf(oldStr);
            returnValue.append(instr, 0, pos).append(newStr);
        }

        return returnValue.toString() + instr;
    }

    public static String getRandPass() {
        Random random = new Random();
        String[] ss = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "J", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            pass.append(ss[random.nextInt(62)]);
        }
        return pass.toString();
    }

    public static byte[] getBase64Bytes(String input) throws UnsupportedEncodingException {
        return getBase64Bytes(input.getBytes("UTF-8"));
    }

    public static byte[] getBase64Bytes(byte[] input) {
        byte[] b = null;

        if (input != null && input.length > 0) {
            b = Base64.encodeBase64(input);
        }

        return b;
    }


    public static String getCurrTimeName() {
        return getCurrTimeName("yyyyMMddddHHmmssSSS");
    }

    public static String getCurrTimeName(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date currentTime = new Date();
        return formatter.format(currentTime);
    }

    public static String getSourcePath() {
        String path = CommonUtil.class.getClassLoader().getResource("").getPath();
        String basePath = path.replace("%20", " ");
        String configPath = CommonUtil.replaceString(basePath, "classes", "source");
        return configPath.substring(1, configPath.length());
    }


    public static void decoder(byte[] bs, String imageURL) {
        try {
            FileOutputStream write = new FileOutputStream(new File(imageURL));
            byte[] decoderBytes = Base64.decodeBase64(Arrays.toString(bs));
            write.write(decoderBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对象转Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    public static String getRandPass(int size) {
        Random random = new Random();
        String[] ss = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < size; i++) {
            pass.append(ss[random.nextInt(10)]);
        }
        return pass.toString();
    }

    public static String base64EncodeString(byte[] input) throws UnsupportedEncodingException {
        byte[] baseBytes = Base64.encodeBase64(input);
        return new String(baseBytes, DEFAULT_ENCODING);
    }

    /**
     * 以当前时间生成唯一数字
     */
    public static String genFileName() {
        StringBuilder fileName = new StringBuilder();
        long time = System.currentTimeMillis();
        int m = (int) (10000 + (Math.random()) * 10000);
        fileName.append(time).append(m);
        return fileName.toString();
    }

    /**
     * 文件数据摘要
     */
    public static String md5File(byte[] fileBytes) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        DigestInputStream dis = new DigestInputStream(new ByteArrayInputStream(fileBytes), messageDigest);
        byte[] buf = new byte[100];
        int len;
        while ((len = dis.read(buf)) != -1) {
            // System.out.println("读取到的数据为：" + new String(buf, 0, len));
        }
        dis.close();
        byte[] digest2 = messageDigest.digest();

        // 当流读取完毕，即将文件读完了， 这时的摘要 才与写入时的 一样
        return toHex(digest2);
    }

    /**
     * md5 摘要转16进制
     */
    private static String toHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        int len = digest.length;

        String out = null;
        for (byte aDigest : digest) {
            // out = Integer.toHexString(0xFF & digest[i] + 0xABCDEF); //加任意 salt
            out = Integer.toHexString(0xFF & aDigest);//原始方法
            if (out.length() == 1) {
                sb.append("0");//如果为1位 前面补个0
            }
            sb.append(out);
        }
        return sb.toString();
    }

    public static byte[] base64DecodeByte(byte[] input) {
        return Base64.decodeBase64(input);
    }

    public static byte[] base64DecodeByte(String input) {
        return Base64.decodeBase64(input);
    }

    public static byte[] uncompressImage(byte[] image) throws Exception {
        byte[] ret = null;

        try {
            String entryName = new String(image, 0, 16);
            int nEnd = entryName.indexOf(0);
            if (nEnd > 0) {
                new String(image, 0, nEnd);
            }

            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(image, 16, image.length - 16));
            if (zis.getNextEntry() != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(500000);
                byte[] bData = new byte[8192];

                int nRet;
                while ((nRet = zis.read(bData)) != -1) {
                    bos.write(bData, 0, nRet);
                }

                bos.close();
                ret = bos.toByteArray();
            }

            zis.close();
            return ret;
        } catch (Exception var9) {
            var9.printStackTrace();
            throw new Exception("Incorrect compressed image file.");
        }
    }
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static boolean checkNum(String number) {
        String rex = "^[1-9]\\d*(\\.\\d+)?$";
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(number);
        return m.find();
    }

    public static String replaceLineFeed(String string) {
        if (string == null || string.length() <= 0) {
            return "";
        }

        if (string.indexOf("%0A") != -1) {
            string = string.replaceAll("%0A", "");
        }
        if (string.indexOf("%0D") != -1) {
            string = string.replaceAll("%0D", "");
        }

        string = string.replaceAll("\r", "");
        string = string.replaceAll("\t", "");
        string = string.replaceAll("\n", "");
        return string;
    }
//    public static void main(String[] args) throws Exception {
//        //加密
//        String a=  CommonUtil.base64EncodeString(   AESUtil.encrypt("ewogICAgInZlcnNpb24iOiIxLjAiLAogICAgInVzZXIiOiLljJfkuqzlronor4HpgJrmtYvor5Xpobnnm64iLAogICAgImNvbmN1cnJlbmN5IjoxMDAsCiAgICAidXNlcmNvdW50IjoxMDAsCiAgICAiYmVnaW50aW1lIjoiMjAyMC0xMS0yIiwKICAgICJlbmR0aW1lIjoiMjAyMS0xMS0zMCIKfQ==".getBytes("UTF-8")));
//        System.out.println(a);
//        //解密
//        String b= new String(CommonUtil.base64DecodeByte(AESUtil.decrypt("azt#Sign@4091QdL",CommonUtil.base64DecodeByte(a))));
//        System.out.println(b);
//    }

    /**
     * 获取证书信息中的CN信息
     *
     * @param certInfo
     * @param InfoType 信息类型
     * @return
     */
    public static String getCN(String certInfo, String InfoType) {
        Map map = new HashMap();
        String[] sub = certInfo.split(",");
        for (int i = 0, len = sub.length; i < len; i++) {
            String[] temp = sub[i].split("=");
            map.put(temp[0].trim(), temp[1]);
        }
        return (String) map.get(InfoType);
    }

}
