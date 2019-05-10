package com.mmall.controller.wx;


import com.alibaba.fastjson.JSONObject;
import com.google.common.primitives.Bytes;
import com.mmall.common.ServerResponse;
import com.mmall.service.IWxService;
import com.mmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wx/")
public class wxController {

    @Autowired
    private IWxService iWxService;

    @RequestMapping(value = "getOpenId",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> getOpenId(String code){
        if(code==null){
            return ServerResponse.createByErrorMeaasge("无法获取openId");
        }
        return iWxService.getOpenId(code);
    }
}
