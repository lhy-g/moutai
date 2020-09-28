package com.tongchuang.general.modular.main.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tongchuang.general.modular.main.entity.User;
import com.tongchuang.general.modular.main.model.dto.SynInfoFormDTO;
import com.tongchuang.general.modular.main.model.dto.UserQueryDTO;
import com.tongchuang.general.modular.main.model.vo.SynInfoVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import com.tongchuang.general.modular.wechat.model.vo.WxPhone;

public interface IUserService extends IService<User> {

    SynInfoVO synInfo(SynInfoFormDTO synInfoFormDto);

    IPage<UserVO> userList(UserQueryDTO userQuery);
    
    /**
     * 兼容[main_user]表里数据
     */
    SynInfoVO compatibleMainUser(SynInfoFormDTO synInfoFormDto,WxPhone wxPhone);
}
