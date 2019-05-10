package com.mmall.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class ConnectHttpUtil {
//    public static void main(String[] args) {
//        ConnectHttpUtil connectHttpUtil=new ConnectHttpUtil();
//        String code = "071W8Dx90mwxuy1UPkw901AHx90W8Dxo";
//        JSONObject result = connectHttpUtil.getOpenId(code);
//        result.
//        System.out.print(result.getBytes("openid"));
//    }


    public JSONObject getOpenId(String code) {
        if(code==null){
            return null;
        }
        JSONObject jsonObject=null;
        BufferedReader in = null;
        //appid和secret是开发者分别是小程序ID和小程序密钥，开发者通过微信公众平台-》设置-》开发设置就可以直接获取，
        String url=PropertiesUtil.getProperty("wx.url")+"?appid="
                +PropertiesUtil.getProperty("wx.appId")+"&secret="+PropertiesUtil.getProperty("wx.secret")+"&js_code="+code+"&grant_type=authorization_code";
        try {
            URL weChatUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = weChatUrl.openConnection();
            // 设置通用的请求属性
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            jsonObject=JSONObject.parseObject(sb.toString());
            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 使用finally块来关闭输入流
	        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}