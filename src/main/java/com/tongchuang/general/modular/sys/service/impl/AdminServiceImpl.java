package com.tongchuang.general.modular.sys.service.impl;

import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tongchuang.general.core.constant.ResEnum;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.AdminContextHolder;
import com.tongchuang.general.core.utils.JwtUtils;
import com.tongchuang.general.core.utils.RequestContextUtils;
import com.tongchuang.general.modular.sys.entity.Admin;
import com.tongchuang.general.modular.sys.mapper.AdminMapper;
import com.tongchuang.general.modular.sys.model.dto.AdminQueryDTO;
import com.tongchuang.general.modular.sys.model.dto.AdminUpdatePasswordDTO;
import com.tongchuang.general.modular.sys.model.dto.LoginFormDTO;
import com.tongchuang.general.modular.sys.model.dto.AdminFormDTO;
import com.tongchuang.general.modular.sys.model.vo.LoginVO;
import com.tongchuang.general.modular.sys.model.vo.AdminVO;
import com.tongchuang.general.modular.sys.service.IAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginFormDTO loginFormDto) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Admin::getUsername, loginFormDto.getUsername());
        Admin admin = getOne(queryWrapper);

        if(admin == null){
            throw new BizException("用户不存在").end(ResEnum.NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginFormDto.getPassword(), admin.getPassword())) {
            throw new BizException("密码错误").end(ResEnum.PARAMETER_ERROR);
        }

        admin.setLastLoginIp(ServletUtil.getClientIP(RequestContextUtils.getCurrentRequest()));
        admin.setLastLoginTime(LocalDateTime.now());

        if(updateById(admin)){
            AdminVO adminVo = new AdminVO();
            BeanUtils.copyProperties(admin, adminVo);
            String token = JwtUtils.generateToken(adminVo);
            return LoginVO.builder()
                    .token(token)
                    .build();
        }
        throw new BizException(ResEnum.UNKNOWN_ERROR);
    }

    @Override
    public IPage<AdminVO> adminList(AdminQueryDTO adminQueryDto) {
        QueryWrapper<Admin> wrapper = new QueryWrapper();

        if(Objects.nonNull(adminQueryDto.getPhone())) {
            wrapper.lambda().eq(Admin::getPhone, adminQueryDto.getPhone());
        } else if(Objects.nonNull(adminQueryDto.getTimeFrame())){
            if(Objects.nonNull(adminQueryDto.getTimeFrame().getStartTime())){
                wrapper.lambda().eq(Admin::getCreateTime, adminQueryDto.getTimeFrame().getStartTime());
            }else if(Objects.nonNull(adminQueryDto.getTimeFrame().getEndTime())){
                wrapper.lambda().eq(Admin::getCreateTime, adminQueryDto.getTimeFrame().getEndTime());
            }
        }

        Page<Admin> admins = page(adminQueryDto.getPage().getObj(), wrapper);
        Page<AdminVO> adminVos = new Page<>();
        BeanUtils.copyProperties(admins, adminVos);
        return adminVos;
    }

    @Override
    public boolean adminInsert(AdminFormDTO adminFormDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminFormDTO, admin);
        admin.setPassword(passwordEncoder.encode(adminFormDTO.getPassword()));
        admin.setCreateTime(LocalDateTime.now());
        return save(admin);
    }

    @Override
    public boolean adminUpdate(AdminFormDTO adminFormDto) {
        Admin admin = new Admin();
        if(adminFormDto.getPassword() != null){
            adminFormDto.setPassword(passwordEncoder.encode(adminFormDto.getPassword()));
        }
        BeanUtils.copyProperties(adminFormDto, admin);
        return updateById(admin);
    }

    @Override
    public boolean adminDelete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean adminUpdatePassword(AdminUpdatePasswordDTO adminUpdatePassword) {
        Admin admin = getById(AdminContextHolder.get().getId());
        if (!passwordEncoder.matches(adminUpdatePassword.getOldPassword(), admin.getPassword())) {
            throw new BizException("原密码不正确").end(ResEnum.PARAMETER_ERROR);
        }
        admin.setPassword(passwordEncoder.encode(adminUpdatePassword.getNewPassword()));
        return updateById(admin);
    }
}
