package com.tongchuang.general.modular.main.service.impl;

import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tongchuang.general.core.constant.ResEnum;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.JwtUtils;
import com.tongchuang.general.modular.main.entity.User;
import com.tongchuang.general.modular.main.mapper.UserMapper;
import com.tongchuang.general.modular.main.model.dto.SynInfoFormDTO;
import com.tongchuang.general.modular.main.model.dto.UserQueryDTO;
import com.tongchuang.general.modular.main.model.vo.SynInfoVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import com.tongchuang.general.modular.main.service.IUserService;
import com.tongchuang.general.modular.wechat.model.vo.WxPhone;
import com.tongchuang.general.modular.wechat.service.IWxAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public SynInfoVO synInfo(SynInfoFormDTO synInfoFormDto) {
        String wxres = WxMaCryptUtils.decrypt(synInfoFormDto.getSessionKey(), synInfoFormDto.getEncryptedData(), synInfoFormDto.getIv());
        WxPhone wxPhone = JSONObject.parseObject(wxres, WxPhone.class);

        if(Objects.isNull(wxPhone)){
            throw new BizException("未找到手机号").end(ResEnum.NOT_FOUND);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getPhone, wxPhone.getPhoneNumber());
        User user = getOne(queryWrapper);
        if (user == null) {
            user = new User();
            user.setCreateTime(LocalDateTime.now());
        }
        BeanUtils.copyProperties(synInfoFormDto, user);
        user.setPhone(wxPhone.getPhoneNumber());

        if (saveOrUpdate(user)) {
            UserVO userVo = new UserVO();
            BeanUtils.copyProperties(user, userVo);
            return SynInfoVO.builder()
                    .token(JwtUtils.generateToken(userVo))
                    .build();
        }

        throw new BizException(ResEnum.UNKNOWN_ERROR);
    }

    @Override
    public IPage<UserVO> userList(UserQueryDTO userQuery) {
        return baseMapper.userList(userQuery.getPage().getObj(), userQuery);
    }
    

	@Override
	public SynInfoVO compatibleMainUser(SynInfoFormDTO synInfoFormDto, WxPhone wxPhone) {
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getPhone, wxPhone.getPhoneNumber());
        User user = getOne(queryWrapper);
        if (user == null) {
            user = new User();
            user.setCreateTime(LocalDateTime.now());
        }
        BeanUtils.copyProperties(synInfoFormDto, user);
        user.setPhone(wxPhone.getPhoneNumber());

        if (saveOrUpdate(user)) {
            UserVO userVo = new UserVO();
            BeanUtils.copyProperties(user, userVo);
            return SynInfoVO.builder()
                    .token(JwtUtils.generateToken(userVo))
                    .build();
        }

        throw new BizException(ResEnum.UNKNOWN_ERROR);
	}
}
