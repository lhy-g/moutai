package com.tongchuang.general.modular.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tongchuang.general.modular.wechat.model.vo.JsCode2Session;
import com.tongchuang.general.modular.wechat.service.IWxAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WxAuthServiceImpl implements IWxAuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public JsCode2Session getJscode2session(String jsCode) {
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", "wx184132c59d008055", "e11f30789c36f2eb6507a0b4ee58a72e", jsCode);
        String resStr = restTemplate.getForObject(url, String.class);
        JSONObject res = JSONObject.parseObject(resStr);
        JsCode2Session jsCode2Session = new JsCode2Session();
        jsCode2Session.setOpenId(res.getString("openid"));
        jsCode2Session.setSessionKey(res.getString("session_key"));
        jsCode2Session.setUnionId(res.getString("unionid"));
        jsCode2Session.setErrCode(res.getInteger("errcode"));
        jsCode2Session.setErrMsg(res.getString("errmsg"));
        return jsCode2Session;
    }
}
