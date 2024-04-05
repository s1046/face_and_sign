package com.hbjc.facce.ctrl;

import com.hbjc.facce.model.Constant;
import com.hbjc.facce.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class IndexCtrl {

    @Autowired
    private MyService myService;

    @GetMapping
    public String index(String templateId, String reportId, HttpServletRequest request){
        Map<String, Object> stringObjectMap = myService.loadTemplateConfig(templateId);
        String interfacesType = stringObjectMap.get("InterfacesType").toString();
        request.setAttribute("templateId",templateId);
        request.setAttribute("reportId",reportId);
        if(interfacesType.equals(Constant.AZT_SingleSign)){  //批量签章传单文件
            return "azt_single_sign";
        }else if(interfacesType.equals(Constant.AZT_SignApply)){ //手动签章
            return "azt_sign_apply";
        }else if(interfacesType.equals(Constant.JC_Archive)){  //直接把pdf传给workfine
            return "jc_archive";
        }
        throw new RuntimeException("interfacesType错误");


    }
}
