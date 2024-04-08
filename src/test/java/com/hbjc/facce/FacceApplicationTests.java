package com.hbjc.facce;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbjc.facce.ctrl.azt.SignCtrl;
import com.hbjc.facce.model.UserModel;
import com.hbjc.facce.resp.ApiResult;
import com.hbjc.facce.resp.MultipleSignResp;
import com.hbjc.facce.resp.SignApplyResp;
import com.hbjc.facce.service.MyService;
import com.hbjc.facce.utils.AztUtils;
import com.hbjc.facce.utils.AztUtilsV2;
import com.hbjc.facce.utils.HttpClientUtils;
import com.hbjc.facce.utils.HttpClientUtilsV2;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@SpringBootTest
@Slf4j
class FacceApplicationTests {

    @Autowired
    private SignCtrl signCtrl;

    @Autowired
    private MyService myService;

    @Autowired
    private Configuration configuration;

    @Value("${filePath}")
    private String filePath;




    @Test
    void loadUsers() {
        List<Map<String, Object>> mapList = myService.loadAllUsers();
        List<UserModel> jsonArray = JSONArray.parseArray(JSONObject.toJSONString(mapList), UserModel.class);
        System.out.println(jsonArray);
    }



    @Test
    void signApply() throws IOException, URISyntaxException {
        String json = AztUtils.readDebugJson("debugJson/request/签章申请.json5");
        String send = HttpClientUtils.send(AztUtils.ICLOUD_DOC_PREFIX + "/apply/v1/signApply", json);
        ApiResult<SignApplyResp> apiResult = JSONObject.parseObject(send, ApiResult.class);
        log.info("签章申请响应信息：{}", JSON.toJSONString(apiResult));
    }



   @Test
   public void templateTest() throws Exception {
        Template template=configuration.getTemplate("request/签章申请.json5");
        Map<String,Object> root=new HashMap<>();
        root.put("fileId","123456");
        Writer out = new StringWriter();
        template.process(root, out);
         // 输出结果
         System.out.println(out.toString());
   }


    @Test
    public void multipleSignTest() throws Exception {
        Template template=configuration.getTemplate("request/多文件签章.json5");
        byte[] fileContent= Files.readAllBytes(Paths.get(filePath+ File.separator+"112.pdf"));
        String base64Content= Base64.getEncoder().encodeToString(fileContent);
        Map<String,Object> root=new HashMap<>();
        root.put("fileData",base64Content);
        Writer out = new StringWriter();
        template.process(root, out);
        String json = AztUtilsV2.readDebugJson("debugJson/request/多文件签章.json5");
        String send = HttpClientUtilsV2.send(AztUtilsV2.ICLOUD_DOC_PREFIX + "/websign/v2/multi/sign", out.toString());
        Map map = JSON.parseObject(send, Map.class);
        if(map.get("code").equals("0")){
            JSONArray signResultList =(JSONArray) map.get("signResultList");
            JSONObject jsonObject = (JSONObject) signResultList.get(0);
            myService.Base64ToFile(jsonObject.getString("signedPdfBase"),"C:/uploads/aa.pdf");
        }
        ApiResult<MultipleSignResp> apiResult = JSONObject.parseObject(send, ApiResult.class);
        log.info("签章申请响应信息：{}", JSON.toJSONString(apiResult));
    }



    @Test
    public void downloadFileTest() throws URISyntaxException, IOException {
        String json = AztUtils.readDebugJson("debugJson/request/文件下载.json5");
        byte[] download = HttpClientUtils.download(AztUtils.ICLOUD_DOC_PREFIX + "/file/v1/download", json);
        FileUtils.writeByteArrayToFile(new File("C:\\uploads\\test3.pdf"), download);
    }



}
