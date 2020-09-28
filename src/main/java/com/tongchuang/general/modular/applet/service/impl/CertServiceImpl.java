package com.tongchuang.general.modular.applet.service.impl;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.DateUtils;
import com.tongchuang.general.core.utils.MyKeyPariUtils;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.modular.applet.constant.CodeConstant;
import com.tongchuang.general.modular.applet.service.AppletUserService;
import com.tongchuang.general.modular.applet.service.CertService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CertServiceImpl  implements CertService{
	@Autowired
	private AppletUserService userService;
	
	@Override
	public String sign(Object obj, String userId) throws Exception {
		PrivateKey privateKey = MyKeyPariUtils.getPrivateKey(userService.getUserPrivateKey(userId));
		String sign = MyKeyPariUtils.sign(obj,privateKey );
		return sign;
	}

	@Override
	public void verify(Object obj, String uniond, String sign){
		try {
			boolean result = MyKeyPariUtils.verify(obj, MyKeyPariUtils.getPublicKey(userService.getUserPublicKey(uniond)), sign);
			if(!result) {
				throw new BizException("签字验证失败,请稍后再试");
			}
		} catch (Exception e) {
			throw new BizException("签字验证错误,请稍后再试");
		}
	}

	@Override
	public boolean certVerify(String data) throws Exception {
		Map<String, String> map = JSON.parseObject(data, LinkedHashMap.class);
		String sign = map.remove(CodeConstant.SIGN);
		String myUnionId = UserContextHolder.getUnionId();
		String unionId = map.get(CodeConstant.APPLICANT);
		verify(map, myUnionId, sign);
		if(unionId.equals(myUnionId)) {
			return true;
		};
		return false;
	}
	
	@Override
	public boolean certVerify2(String data,String userId,String sign) throws Exception {
		verify(data, userId, sign);
		return true;
	}
	
	@Override
	public void requestCertVerify(String data) throws Exception {
		Map<String, String> map = JSON.parseObject(data, LinkedHashMap.class);
		String myUnionId = UserContextHolder.getUnionId();
		String sign = map.remove(CodeConstant.SIGN);
		String unionId = map.get(CodeConstant.APPLICANT);
		Long time1 = Long.valueOf(map.get(CodeConstant.TIME_STAMP));
		Long time2 = Long.valueOf(DateUtils.getTimeStamp());
		if(time2-time1>20) {
			throw new BizException("证书已过期");
		}
		if(!unionId.equals(myUnionId)) {
			throw new BizException("非法证书");
		}
		verify(map, unionId, sign);
//		if(!verify(map, unionId, sign)) {
//			throw new BizException("证书验证错误!"); 
//		};
		 
	}
	
	@Override
	public Map<String,String> dataStringToMap(String str){
		str=str.trim();
		if(StringUtils.isEmpty(str)) {
			throw new BizException("获得扫码为空!");
		}
		if(str.indexOf("+a")>0||str.indexOf("+b")>0||str.indexOf("+c")>0||str.indexOf("+s")>0) {
			str = str.replaceAll("\\+", "");
		}
		log.info("获得扫码参数:" + str);
		str=str.substring(1, str.length()-1);
		String[] strs=str.split(",");
		Map<String,String> map = new HashMap<String, String>();
		for (String string : strs) {
			if(string.indexOf("=")>=1) {
				String key=string.split("=")[0].trim();
				String value=string.split("=")[1];
				map.put(key, value);
			}else {
				log.info("扫码参数解析错误:"+string);
			}
		}
		return map;
	}
 
}










