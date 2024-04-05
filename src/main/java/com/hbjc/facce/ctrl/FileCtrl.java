package com.hbjc.facce.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.hbjc.facce.model.Constant;
import com.hbjc.facce.resp.ApiResult;
import com.hbjc.facce.resp.UploadFileResp;
import com.hbjc.facce.service.MyService;
import com.hbjc.facce.utils.AztUtils;
import com.hbjc.facce.utils.HttpClientUtils;
import com.hbjc.facce.utils.Md5Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Slf4j
public class FileCtrl {

    @Value("${filePath}")
    private String filePath;

    @Value("${downloadPath}")
    private String downloadPath;

    @Autowired
    private MyService myService;

    @Autowired
    private Cache<String,String> cache;
    /**
     * 读取到文件返回到文件端口
     * @param fileName
     * @param response
     */
    @GetMapping("/readFile")
    @CrossOrigin
    public void readFile(String fileName, HttpServletResponse response) throws  Exception{
        FileInputStream inputStream=new FileInputStream(new File(downloadPath,fileName));
        byte[] bytes=new byte[1024];
        int byteRead;
        OutputStream fileOutputStream=response.getOutputStream();
        while ((byteRead=inputStream.read(bytes))!=-1){
            fileOutputStream.write(bytes,0,byteRead);
        }
        inputStream.close();
    }


    /**
     * 0.workfile传递文件给中转程序
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @CrossOrigin
    @PostMapping("/sendfile")
    public String sendfile(MultipartFile file, HttpServletRequest request) throws Exception {
        String  templateId= Optional.ofNullable(request.getParameter("templateId")).orElse("1");
        String  reportId= Optional.ofNullable(request.getParameter("reportId")).orElse("1");
        String uniqueName=templateId.concat("_").concat(reportId);
        String originFile=file.getOriginalFilename();
        log.info("接收到pdf文件:{}",originFile);
        log.info("接受到的所有参数：{}", JSON.toJSONString(request.getParameterMap()));
        String data = request.getParameter("data");
        Map<String, Object> stringObjectMap = myService.loadTemplateConfig(templateId);
        String interfacesType = stringObjectMap.get("InterfacesType").toString();
        //当没有data表单内容的时候，就手动模拟下数据
        if(data.equals("undefined")){
            if(interfacesType.equals(Constant.AZT_SingleSign)){
                data = AztUtils.readDebugJson("debugJson/request/批量表单数据模拟.json5");
            }else if(interfacesType.equals(Constant.AZT_SignApply)){
                data = AztUtils.readDebugJson("debugJson/request/手动表单数据模拟.json5");
            }
        }
        cache.put(Md5Util.getMd5(uniqueName),data);
        FileUtils.copyInputStreamToFile(file.getInputStream(),new File(filePath, Md5Util.getMd5(uniqueName).concat(".pdf")));
        return "文件上传成功";
    }





}
