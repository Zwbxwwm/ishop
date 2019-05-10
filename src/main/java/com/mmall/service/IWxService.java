package com.mmall.service;

import com.alibaba.fastjson.JSONObject;
import com.mmall.common.ServerResponse;

import java.util.Map;

public interface IWxService {
    ServerResponse<Map> getOpenId(String code);
}
