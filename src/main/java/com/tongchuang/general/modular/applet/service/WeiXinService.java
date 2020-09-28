package com.tongchuang.general.modular.applet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tongchuang.general.modular.main.model.dto.SynInfoFormDTO;
import com.tongchuang.general.modular.main.model.dto.UserQueryDTO;
import com.tongchuang.general.modular.main.model.vo.SynInfoVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import com.tongchuang.general.modular.wechat.model.vo.JsCode2Session;


public interface WeiXinService {

	JsCode2Session getJscode2session(String jsCode);

	SynInfoVO synInfo(SynInfoFormDTO synInfoFormDto);

	//IPage<UserVO> userList(UserQueryDTO userQuery);
}
