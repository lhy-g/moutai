package com.tongchuang.general.modular.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tongchuang.general.modular.sys.entity.Admin;
import com.tongchuang.general.modular.sys.model.dto.AdminQueryDTO;
import com.tongchuang.general.modular.sys.model.dto.AdminUpdatePasswordDTO;
import com.tongchuang.general.modular.sys.model.dto.LoginFormDTO;
import com.tongchuang.general.modular.sys.model.dto.AdminFormDTO;
import com.tongchuang.general.modular.sys.model.vo.LoginVO;
import com.tongchuang.general.modular.sys.model.vo.AdminVO;

import java.util.List;

public interface IAdminService extends IService<Admin> {

    LoginVO login(LoginFormDTO loginFormDTO);

    IPage<AdminVO> adminList(AdminQueryDTO adminQueryDto);

    boolean adminInsert(AdminFormDTO adminFormDTO);

    boolean adminUpdate(AdminFormDTO adminFormDTO);

    boolean adminDelete(Long id);

    boolean adminUpdatePassword(AdminUpdatePasswordDTO adminUpdatePassword);
}
