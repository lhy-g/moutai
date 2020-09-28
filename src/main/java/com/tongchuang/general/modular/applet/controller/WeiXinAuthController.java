package com.tongchuang.general.modular.applet.controller;

import com.tongchuang.general.core.annotation.OpenMapping;
import com.tongchuang.general.core.utils.JwtUtils;
import com.tongchuang.general.core.utils.MyCodeUtil;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.applet.constant.CodeConstant;
import com.tongchuang.general.modular.applet.service.WeiXinService;
import com.tongchuang.general.modular.main.model.dto.SynInfoFormDTO;
import com.tongchuang.general.modular.main.model.vo.SynInfoVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import com.tongchuang.general.modular.wechat.model.vo.JsCode2Session;
import com.tongchuang.general.modular.wechat.service.IWxAuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags ="2.3微信授权接口")
@RequestMapping("/applet/login")
@RestController
public class WeiXinAuthController {

    @Autowired
    private WeiXinService weiXinService;
    
    
	@OpenMapping
	@ApiOperation("自定义促销劵")
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public R<String> hello(HttpServletResponse response) throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("b", "2009016868202102");// 老板

//		//map.put(CertConstant.APPLICANT, "rCluoaq4");//放行员/收纳员
//		map.put("js", "rCluoaq4");//驾驶员
//		map.put("t", "1596783772");
//		map.put("l", "113.123456");
		map.put("t", "1599639078");
		map.put("s",
				"TL7QhaQmeHSA6mWXCmu-eXyMXCYXK316B7eNPrNGwPv7DQLpI9gFuYxVYqJEqmjU8atVgOvcfuP4AcHZOLG6Swu5l0xgDu1PtAF9/bnP4TmcyH-nrCMCL21okxtGJIIWCG/pJp1vDMpSJqqxqA/-Y5BCS7KgSfmQJ9O6i/t/GT8=");
		String url = CodeConstant.URL + map;
		// String url = "https://maotaiapi.lcpower.cn?number=1051";
		response.addHeader("Content-Type", "text/plain;charset=UTF-8");
		response.addHeader("Content-Type", "image/jpg");
		byte[] fimalByte = MyCodeUtil.generateQRCodeToByte(url, "201");
		response.getOutputStream().write(fimalByte);
		return R.success().setData("hello !!!");
	}
    
    @OpenMapping
    @GetMapping("/{jsCode}")
    public R<JsCode2Session> getJscode2session(@PathVariable String jsCode){
        return R.success().setData(weiXinService.getJscode2session(jsCode));
    }
    
    @OpenMapping
    @ApiOperation("同步用户信息或者注册用户信息")
    @PostMapping("/syn/info")
    public R<SynInfoVO> synInfo(@RequestBody SynInfoFormDTO synInfoFormDto) {
        return R.success().setData(weiXinService.synInfo(synInfoFormDto));
    }

    @ApiOperation("当前信息")
    @GetMapping("/current/info")
    public R<UserVO> findCurrentInfo() {
        return R.success().setData(UserContextHolder.get());
    }
    
   
}
