package com.tongchuang.general.modular.main.controller;

import com.tongchuang.general.core.annotation.OpenMapping;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.core.web.controller.BaseController;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.main.model.dto.SynInfoFormDTO;
import com.tongchuang.general.modular.main.model.vo.SynInfoVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import com.tongchuang.general.modular.main.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户接口")
@RequestMapping("/main/user")
@RestController
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @OpenMapping
    @ApiOperation("同步信息")
    @PostMapping("/syn/info")
    public R<SynInfoVO> synInfo(@RequestBody SynInfoFormDTO synInfoFormDto) {
        return R.success().setData(userService.synInfo(synInfoFormDto));
    }

    @ApiOperation("当前信息")
    @GetMapping("/current/info")
    public R<UserVO> findCurrentInfo() {
        return R.success().setData(UserContextHolder.get());
    }
}
