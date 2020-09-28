package com.tongchuang.general.modular.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tongchuang.general.modular.main.entity.User;
import com.tongchuang.general.modular.main.model.dto.UserQueryDTO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

    IPage<UserVO> userList(Page page, @Param("userQuery") UserQueryDTO userQuery);
}
