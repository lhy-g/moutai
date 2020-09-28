package com.tongchuang.general.modular.applet.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tongchuang.general.core.constant.ResEnum;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.JwtUtils;
import com.tongchuang.general.modular.applet.entity.UserInfo;
import com.tongchuang.general.modular.applet.mapper.AppletUserMapper;
import com.tongchuang.general.modular.applet.service.WeiXinService;
import com.tongchuang.general.modular.main.entity.User;
import com.tongchuang.general.modular.main.model.dto.SynInfoFormDTO;
import com.tongchuang.general.modular.main.model.dto.UserQueryDTO;
import com.tongchuang.general.modular.main.model.vo.SynInfoVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import com.tongchuang.general.modular.main.service.IUserService;
import com.tongchuang.general.modular.main.service.impl.UserServiceImpl;
import com.tongchuang.general.modular.wechat.model.vo.JsCode2Session;
import com.tongchuang.general.modular.wechat.model.vo.WxPhone;

import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;

@Service
public class WeixinServiceImpl extends ServiceImpl<AppletUserMapper, UserInfo> implements WeiXinService {
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AppletUserMapper appletUserMapper;
	
	private IUserService userService;

	@Override
	public JsCode2Session getJscode2session(String jsCode) {
		String url = String.format(
				"https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
				"wx184132c59d008055", "76cca03447505921e98bb44f2efbd4fb", jsCode);
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

	@Override
	public SynInfoVO synInfo(SynInfoFormDTO synInfoFormDto) {
		String wxres = WxMaCryptUtils.decrypt(synInfoFormDto.getSessionKey(), synInfoFormDto.getEncryptedData(),
				synInfoFormDto.getIv());
		WxPhone wxPhone=null;
		try {
			wxPhone = JSONObject.parseObject(wxres, WxPhone.class);
		} catch (Exception e) {
			throw new BizException("请重试!");
		}

		if (Objects.isNull(wxPhone)) {
			throw new BizException("未找到手机号").end(ResEnum.NOT_FOUND);
		}
		//兼容老用户,判断是否为老用户
//		User userOld=appletUserMapper.queryUserByPhone(wxPhone.getPhoneNumber());
//		if(null != userOld) {
//			userService.compatibleMainUser(synInfoFormDto, wxPhone);
//		}
		 
		UserInfo userInfo = appletUserMapper.queryUserInfoByPhone(synInfoFormDto.getUnionId());
		if(null == userInfo) {
			//userInfo = appletUserMapper.queryUserInfoByPhone2(wxPhone.getPhoneNumber());
		}
		if (userInfo == null) {
			userInfo = new UserInfo();
			userInfo.setUnionId(synInfoFormDto.getUnionId());
			userInfo.setType("0");
			userInfo.setStatus("1");
		}
		if(StringUtils.isEmpty(userInfo.getUserId())) {
			userInfo.setUserId(synInfoFormDto.getUnionId().substring(20));
		}
		userInfo.setNicename(synInfoFormDto.getNickname());
		userInfo.setPhone(wxPhone.getPhoneNumber());
		userInfo.setHeadUrl(synInfoFormDto.getHeadPortraitLink());
		BeanUtils.copyProperties(synInfoFormDto, userInfo);
		int count = appletUserMapper.updateUserInfo(userInfo);
		if (count==1||count==2) {
			UserVO userVo = new UserVO();
			BeanUtils.copyProperties(userInfo, userVo);
			userVo.setHeadPortraitLink(userInfo.getHeadUrl());
			return SynInfoVO.builder().token(JwtUtils.generateToken(userVo)).build();
		}
		throw new BizException(ResEnum.UNKNOWN_ERROR);
	}

//	@Override
//	public IPage<UserVO> userList(UserQueryDTO userQuery) {
//		return baseMapper.userList(userQuery.getPage().getObj(), userQuery);
//	}

}
