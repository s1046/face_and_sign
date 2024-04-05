package com.hbjc.facce.ctrl.jc;

import com.hbjc.facce.service.MyService;
import com.hbjc.facce.utils.Md5Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class JcCtrl {

    @Autowired
    private MyService myService;

    @Value("${filePath}")
    private String filePath;

    @Value("${downloadPath}")
    private String downloadPath;

    @Value("${serverPdfUrl}")
    private String serverPdfUrl;


    @PostMapping("/loadJcPdf")
    public Map<String,Object> loadJcPdf(@RequestBody Map params) throws IOException {
        String  reportId= params.get("reportId").toString();
        String templateId=params.get("templateId").toString();
        String uniqueName= Md5Util.getMd5(templateId.concat("_").concat(reportId));
        Map<String,Object> configMap=myService.loadTemplateConfig(templateId);
        //附件回写id
        Integer uploadFileId =Integer.parseInt(configMap.get("UploadFileId").toString());
        Map<String,Object> returnMap=new HashMap<>();
        returnMap.put("pdf",String.format(serverPdfUrl,uniqueName.concat(".pdf")));
        returnMap.put("uploadFileId",uploadFileId);
        FileUtils.copyFile(new File(filePath,uniqueName.concat(".pdf")),new File(downloadPath,uniqueName.concat(".pdf")));
        return returnMap;
    }
}
