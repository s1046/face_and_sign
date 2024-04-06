package com.hbjc.facce.ctrl.azt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.hbjc.facce.model.FormFieldModel;
import com.hbjc.facce.resp.ApiResult;
import com.hbjc.facce.resp.SignApplyResp;
import com.hbjc.facce.resp.SignResultResp;
import com.hbjc.facce.resp.UploadFileResp;
import com.hbjc.facce.service.MyService;
import com.hbjc.facce.utils.AztUtils;
import com.hbjc.facce.utils.HttpClientUtils;
import com.hbjc.facce.utils.Md5Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 安证通v1接口 走预览签章
 */
@RestController
@Slf4j
public class SignCtrl {

    @Autowired
    private Configuration configuration;

    @Value("${filePath}")
    private String filePath;

    @Value("${downloadPath}")
    private String downloadPath;


    @Autowired
    private Cache<String,String> cache;


    @Autowired
    private MyService myService;

    @Value("${serverPdfUrl}")
    private String serverPdfUrl;




    /**
     * 签章申请的后端接口
     * @param params
     * @return
     */
    @PostMapping("/signApply")
    @CrossOrigin
    public ApiResult<SignApplyResp> signApplyCtrl(@RequestBody Map params) throws TemplateException, IOException {
        String  reportId= params.get("reportId").toString();
        String templateId=params.get("templateId").toString();
        Map<String,Object> configMap=myService.loadTemplateConfig(templateId);
        String uniqueName= Md5Util.getMd5(templateId.concat("_").concat(reportId));
        String data = cache.getIfPresent(uniqueName);
        List<FormFieldModel> list = JSONArray.parseArray(data, FormFieldModel.class);
        Integer YZBMField=Integer.parseInt(configMap.get("YZBMField").toString());
        String sealCode=list.stream().filter(i->i.getId().equals(YZBMField)).findAny().get().getContent();
        Map<String, Object> signMapObj = myService.loadSignConfig(sealCode);
        String fileId=params.get("fileId").toString();
        String businessId = params.get("businessId").toString();
        Template template=configuration.getTemplate("request/签章申请.json5");
        Map<String,Object> root=new HashMap<>();
        root.put("fileId",fileId);
        root.put("businessId",businessId);
        root.put("orgCode",signMapObj.get("Code").toString());
        root.put("orgName",signMapObj.get("DWMC").toString());
        Writer out = new StringWriter();
        template.process(root, out);
        log.info("签章申请时的参数是:{}",out.toString());
        String send = HttpClientUtils.send(AztUtils.ICLOUD_DOC_PREFIX + "/apply/v1/signApply", out.toString());
        ApiResult<SignApplyResp> apiResult = JSONObject.parseObject(send, ApiResult.class);
        log.info("签章申请响应信息：{}", JSON.toJSONString(apiResult));
        return  apiResult;
    }


    /**
     * 查询签章结果
     * @param params
     * @return
     * @throws TemplateException
     * @throws IOException
     */
    @GetMapping("/getSignResult/{businessId}")
    @CrossOrigin
    public ApiResult<SignResultResp> getSignResult(@PathVariable String businessId) throws TemplateException, IOException, URISyntaxException {
        Template template=configuration.getTemplate("request/签章申请结果查询.json5");
        Map<String,Object> root=new HashMap<>();
        root.put("businessId",businessId);
        Writer out = new StringWriter();
        template.process(root, out);
        String send = HttpClientUtils.send(AztUtils.ICLOUD_DOC_PREFIX + "/apply/v1/getSignResult", out.toString());
        ApiResult<SignResultResp> apiResult = JSONObject.parseObject(send, ApiResult.class);
        log.info("签章申请结果响应信息：{}", JSON.toJSONString(apiResult));
        return  apiResult;
    }






    /**
     *
     * {
     *   "fileName": "测试.pdf",
     *   "fileSource": "4",
     *   "businessId": "20237612011464bg44",
     *   "fileUrl": "",
     *   "expiryTime": 24,     *
     *   "fileFormat": "pdf"
     * }
     * 1.上传文件给安整通
     * @return
     */
    @PostMapping("/uploadFile2Azt")
    @CrossOrigin
    public ApiResult uploadFile2Azt(@RequestBody Map params) throws  IOException {
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
        String[] multipleFileId = fileId.split(",");
        Map<String,String> map=new HashMap<>();
        map.put("fileName", UUID.randomUUID().toString().replace("-","").substring(0,32).concat(".pdf"));
        map.put("fileSource","4");
        map.put("businessId", uniqueName);
        //todo这里的文件需要动态传递
        String fileName=uniqueName.concat(".pdf");
        //首次签章 或者 只有一次签章的只需要每次来着蒌一个新胡pdf
        if(StringUtils.isEmpty(attachFormModel.getContent())|| multipleFileId.length==1){
            byte[] fileContent= Files.readAllBytes(Paths.get(filePath+File.separator+fileName));
            String base64Content= Base64.getEncoder().encodeToString(fileContent);
            map.put("fileContent",base64Content);
        }else {
            byte[] fileContent= Files.readAllBytes(Paths.get(downloadPath+File.separator+fileName));
            String base64Content= Base64.getEncoder().encodeToString(fileContent);
            map.put("fileContent",base64Content);
        }
        map.put("expiryTime","24");
        map.put("fileFormat","pdf");
        String json= JSON.toJSONString(map);
        String send = HttpClientUtils.send(AztUtils.ICLOUD_DOC_PREFIX + "/file/v1/upload", json);
        ApiResult<UploadFileResp> apiResult = JSONObject.parseObject(send, ApiResult.class);
        //{"code":200,"data":{"fileName":"测试.pdf","pageCount":1,"businessId":"85a33c75593b4cda96276a1a25424ac1","format":"pdf","scale":"2","fileId":"507d16b0c2034ef49ed9c4338dffcada"},"msg":"成功"}
        return apiResult;
    }


    /**
     * 下载安证通签章好胡文件
     * @return
     */
    @PostMapping("/downLoadAztFile")
    public Map<String, Object> downLoadAztFile(@RequestBody Map params,HttpServletRequest request)throws Exception{
        String  reportId= params.get("reportId").toString();
        String templateId=params.get("templateId").toString();
        String fileId=params.get("fileId").toString();
        String uniqueName=Md5Util.getMd5(templateId.concat("_").concat(reportId));
        Map<String,Object> configMap=myService.loadTemplateConfig(templateId);
        Integer uploadFileId =Integer.parseInt(configMap.get("UploadFileId").toString());
        File sotoreFile=new File(downloadPath,uniqueName.concat(".pdf"));
        Template template=configuration.getTemplate("request/文件下载.json5");
        Map<String,Object> root=new HashMap<>();
        root.put("fileId",fileId);
        Writer out = new StringWriter();
        template.process(root, out);
        byte[] download = HttpClientUtils.download(AztUtils.ICLOUD_DOC_PREFIX + "/file/v1/download", out.toString());
        FileUtils.writeByteArrayToFile(sotoreFile, download);
        Map<String,Object> respMap=new HashMap<>();
        String host = new URL(request.getRequestURL().toString()).getHost();
        Integer port=request.getServerPort();
        respMap.put("pdf",String.format(serverPdfUrl,host,port,uniqueName.concat(".pdf")));
        respMap.put("uploadFileId",uploadFileId);
        return respMap;
    }









}
