package com.mmall.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mmall.common.ServerResponse;
import com.mmall.service.IWxService;
import com.mmall.util.ConnectHttpUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("iwxService")
public class WxService implements IWxService {


    public ServerResponse<Map> getOpenId(String code){
        ConnectHttpUtil connectHttpUtil = new ConnectHttpUtil();
        Map map = new HashMap();
        JSONObject result = connectHttpUtil.getOpenId(code);
        String openId = result.getString("openid");
        map.put("userId",openId);
        return ServerResponse.createBySuccess(map);
    }

}
