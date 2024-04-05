package com.hbjc.facce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;  
  
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.sql.Types;
import java.util.Base64;
import java.util.List;
import java.util.Map;
  
@Service  
public class MyService {
  
    @Autowired  
    private JdbcTemplate jdbcTemplate;  
  
    public void updateIsActive(int id, int isActive) {  
       String sql="UPDATE tb_sys_user\n" +
               " SET is_active = 1\n" +
               " WHERE id = %s; ";
        String format = String.format(sql, id);
        jdbcTemplate.execute(format);
    }


    public List<Map<String,Object>> loadAllUsers(){
        String sql="SELECT TOP 1000 * FROM [dbo].[View_FaceRecognition]";
         return jdbcTemplate.queryForList(sql);
    }


    /**
     * templteId加载数据
     * @param templateId
     * @return
     */
    public  Map<String,Object> loadTemplateConfig(String templateId){
        String sql="SELECT TOP 1000 * FROM  [dbo].[QZ_FormInterfaces] where templateId=%s";
        String format = String.format(sql, templateId);
        return jdbcTemplate.queryForList(format).get(0);

    }

    /**
     * 由sealCode查询印章编码
     * @param sealCode
     * @return
     */
    public Map<String,Object> loadSignConfig(String sealCode){
        String sql="SELECT TOP 1000 * FROM  [dbo].[QZ_SignatureList] where YZBM='%s'";
        String format = String.format(sql, sealCode);
        return jdbcTemplate.queryForList(format).get(0);
    }


    /**
     *
     * @param base64Str base64字符编码
     * @param targetFilePath 文件存在电脑中的哪个位置(比如传: "D://")
     */
    public  void Base64ToFile(String base64Str, String targetFilePath){
        byte[] buffer = Base64.getDecoder().decode(base64Str); //将base64字符串转为字节
        FileOutputStream out = null; //创建一个FileOutputStream对象
        try{
            out = new FileOutputStream(targetFilePath); //向文件中写入数据
            out.write(buffer); //将字节数据写入到文件中
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != out){
                try {
                    out.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


}