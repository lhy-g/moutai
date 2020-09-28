package com.tongchuang.general.modular.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tongchuang.general.modular.sys.model.dto.AdminQueryDTO;
import com.tongchuang.general.modular.sys.model.dto.AdminUpdatePasswordDTO;
import com.tongchuang.general.modular.sys.model.dto.LoginFormDTO;
import com.tongchuang.general.core.annotation.OpenMapping;
import com.tongchuang.general.core.utils.AdminContextHolder;
import com.tongchuang.general.core.web.controller.BaseController;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.sys.model.dto.AdminFormDTO;
import com.tongchuang.general.modular.sys.model.vo.AdminVO;
import com.tongchuang.general.modular.sys.model.vo.LoginVO;
import com.tongchuang.general.modular.sys.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员接口")
@RequestMapping("/sys/admin")
@RestController
public class AdminController extends BaseController {

    @Autowired
    private IAdminService userService;

    @OpenMapping
    @ApiOperation("登录")
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody LoginFormDTO loginFormDTO) {
        LoginVO loginVo = userService.login(loginFormDTO);
        if (loginVo != null) {
            return R.success().setData(loginVo);
        }
        return R.fail();
    }

    @ApiOperation("当前信息")
    @GetMapping("/current/info")
    public R<AdminVO> findCurrentInfo() {
        return R.success().setData(AdminContextHolder.get());
    }

    @OpenMapping
    @ApiOperation("管理员列表")
    @GetMapping("/list")
    public R<IPage<AdminVO>> adminList(AdminQueryDTO adminQueryDto){
        return R.success().setData(userService.adminList(adminQueryDto));
    }

    @OpenMapping
    @ApiOperation("管理员添加")
    @PostMapping
    public R adminInsert(@RequestBody AdminFormDTO adminFormDTO) {
        if (userService.adminInsert(adminFormDTO)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("管理员更新")
    @PutMapping
    public R adminUpdate(@RequestBody AdminFormDTO adminFormDTO) {
        if (userService.adminUpdate(adminFormDTO)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("管理员删除")
    @DeleteMapping("/{id}")
    public R adminDelete(@PathVariable Long id) {
        if (userService.adminDelete(id)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("管理员统计")
    @GetMapping("/count")
    public R adminCount(){
        return R.success().setData(userService.count());
    }

    @ApiOperation("管理员修改密码")
    @PutMapping("/password")
    public R adminUpdatePassword(@RequestBody AdminUpdatePasswordDTO adminUpdatePassword){
        return R.success().setData(userService.adminUpdatePassword(adminUpdatePassword));
    }
}
