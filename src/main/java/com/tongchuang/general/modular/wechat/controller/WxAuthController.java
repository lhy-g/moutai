package com.tongchuang.general.modular.wechat.controller;

import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.wechat.model.vo.JsCode2Session;
import com.tongchuang.general.modular.wechat.service.IWxAuthService;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiModel("微信授权接口")
@RequestMapping("/wx/auth")
@RestController
public class WxAuthController {

    @Autowired
    private IWxAuthService wxAuthService;

    @GetMapping("/{jsCode}")
    public R<JsCode2Session> getJscode2session(@PathVariable String jsCode){
        return R.success().setData(wxAuthService.getJscode2session(jsCode));
    }
}
