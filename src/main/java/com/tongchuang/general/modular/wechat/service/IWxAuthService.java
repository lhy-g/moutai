package com.tongchuang.general.modular.wechat.service;

import com.tongchuang.general.modular.wechat.model.vo.JsCode2Session;

public interface IWxAuthService {

    JsCode2Session getJscode2session(String jsCode);
}
