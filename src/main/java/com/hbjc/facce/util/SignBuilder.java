package com.hbjc.facce.util;
import com.hbjc.facce.model.ScanFaceInitModel;
import com.hbjc.facce.model.ScanFaceResultModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
public class SignBuilder {
    /**
     * 刷脸初始化的时候
     * @param scanFaceInit
     * @return
     */
    public static String buildSign(ScanFaceInitModel scanFaceInit) throws IllegalAccessException {
        List<String> lists=new ArrayList<>();
        Field[] declaredFields = scanFaceInit.getClass().getDeclaredFields();
        for(Field field:declaredFields){
            if(!field.getName().equals("sign") && !field.getName().equals("company")){
                if( field.get(scanFaceInit)!=null){
                    lists.add((String) field.get(scanFaceInit));
                }
            }
        }
        Collections.sort(lists);
        String origin = String.join("", lists);
        log.info("原始字符串是:{}",origin);
        return DigestUtils.md5Hex(origin);
    }


    public static String buildSign(ScanFaceResultModel scanFaceInit) throws IllegalAccessException {
        List<String> lists=new ArrayList<>();
        Field[] declaredFields = scanFaceInit.getClass().getDeclaredFields();
        for(Field field:declaredFields){
            if(!field.getName().equals("sign")){
                if( field.get(scanFaceInit)!=null){
                    lists.add((String) field.get(scanFaceInit));
                }
            }
        }
        Collections.sort(lists);
        String origin = String.join("", lists);
        log.info("原始字符串是:{}",origin);
        return DigestUtils.md5Hex(origin);
    }




}
