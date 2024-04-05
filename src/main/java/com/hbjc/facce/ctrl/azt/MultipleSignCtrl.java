package com.hbjc.facce.ctrl.azt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.hbjc.facce.model.FormFieldModel;
import com.hbjc.facce.resp.ApiResult;
import com.hbjc.facce.resp.MultipleSignResp;
import com.hbjc.facce.service.MyService;
import com.hbjc.facce.utils.AztUtilsV2;
import com.hbjc.facce.utils.HttpClientUtils;
import com.hbjc.facce.utils.Md5Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安证通多文件签章接口
 */
@RestController
@Slf4j
public class MultipleSignCtrl {

    @Autowired
    private Configuration configuration;


    @Value("${filePath}")
    private String filePath;


    @Value("${downloadPath}")
    private String downloadPath;

    @Value("${serverPdfUrl}")
    private String serverPdfUrl;

    @Autowired
    private MyService myService;

    @Autowired
    private Cache<String,String> cache;

    /**
     * 多文件签章的多
     * @return
     * @throws Exception
     */
    @PostMapping("/multipleSign")
    public Map<String, Object> multipleSign(@RequestBody Map params) throws Exception {
        String  reportId= params.get("reportId").toString();
        String templateId=params.get("templateId").toString();
        String uniqueName= Md5Util.getMd5(templateId.concat("_").concat(reportId));
        Map<String,Object> configMap=myService.loadTemplateConfig(templateId);
        //xPosition
        String xPosition=configMap.get("xPosition").toString();
        String yPosition=configMap.get("yPosition").toString();
        //附件回写id
        Integer uploadFileId =Integer.parseInt(configMap.get("UploadFileId").toString());
        //签章位置的字段
        String fileId=configMap.get("FieldId").toString();
        Integer YZBMField=Integer.parseInt(configMap.get("YZBMField").toString());
        String data = cache.getIfPresent(uniqueName);
        List<FormFieldModel> list = JSONArray.parseArray(data, FormFieldModel.class);
        String sealCode=list.stream().filter(i->i.getId().equals(YZBMField)).findAny().get().getContent();
        FormFieldModel attachFormModel=list.stream().filter(i->i.getId().equals(uploadFileId)).findAny().get();
        Template template=configuration.getTemplate("request/多文件签章.json5");
        byte[] fileContent= Files.readAllBytes(Paths.get(filePath+ File.separator+uniqueName.concat(".pdf")));
        String base64Content= Base64.getEncoder().encodeToString(fileContent);
        Map<String,Object> root=new HashMap<>();
        root.put("fileData",base64Content);
        root.put("sealCode",sealCode);
        String[] multipleFileId = fileId.split(",");
        String[] multipleXposition=xPosition.split(",");
        String[] multipleYpositon=yPosition.split(",");
        //首次签章 或者 只有一次签章的只需要每次来着蒌一个新胡pdf
        if(StringUtils.isEmpty(attachFormModel.getContent())|| multipleFileId.length==1){
            //多次盖章字段id
            FormFieldModel positionModel = list.stream().filter(i -> i.getId().equals(Integer.parseInt(multipleFileId[0]))).findAny().get();
            root.put("xPosition",positionModel.getX()+Integer.parseInt(multipleXposition[0]));
            root.put("yPosition",positionModel.getY()+Integer.parseInt(multipleYpositon[0]));
        }else {
            //查找到之前已经签章的pdf
             fileContent= Files.readAllBytes(Paths.get(downloadPath+ File.separator+uniqueName.concat(".pdf")));
             base64Content= Base64.getEncoder().encodeToString(fileContent);
             root.put("fileData",base64Content);
             String content=attachFormModel.getContent();
             Map map = JSONObject.parseObject(content, Map.class);
             JSONArray dataArray = (JSONArray)map.get("data");
             if(dataArray.size()>multipleFileId.length){
                int lastIndex=multipleFileId.length-1;
                FormFieldModel positionModel = list.stream().filter(i -> i.getId().equals(Integer.parseInt(multipleFileId[lastIndex]))).findAny().get();
                root.put("xPosition",positionModel.getX()+Integer.parseInt(multipleXposition[lastIndex]));
                root.put("yPosition",positionModel.getY()+Integer.parseInt(multipleYpositon[lastIndex]));
            }
        }
        Writer out = new StringWriter();
        template.process(root, out);
        String send = HttpClientUtils.send(AztUtilsV2.ICLOUD_DOC_PREFIX + "/websign/v2/multi/sign", out.toString());
        Map map = JSON.parseObject(send, Map.class);
        if(map.get("code").equals("0")){
            JSONArray signResultList =(JSONArray) map.get("signResultList");
            JSONObject jsonObject = (JSONObject) signResultList.get(0);
            myService.Base64ToFile(jsonObject.getString("signedPdfBase"),downloadPath+uniqueName.concat(".pdf"));
        }
        Map<String,Object>  returnMap=new HashMap<>();
        returnMap.put("pdf",String.format(serverPdfUrl,uniqueName.concat(".pdf")));
        returnMap.put("uploadFileId",uploadFileId);
        return returnMap;
    }

}
