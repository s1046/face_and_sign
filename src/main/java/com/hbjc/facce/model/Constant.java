package com.hbjc.facce.model;

public interface Constant {
   //安证通批量单个PDF签章接口
    String AZT_SingleSign="AZT_SingleSign";


    //安政通手动签章
    String AZT_SignApply="AZT_SignApply";

    //金财存档接口:收到workfine发送PDF信息后，中间程序不传安证通，转存PDF入workfine
    String JC_Archive="JC_Archive";

    //金财存档接口:收到workfine发送PDF信息后，中间程序不传安证通，转存PDF入workfine(图片签名)
    String JC_SignArchive="JC_SignArchive";


}
