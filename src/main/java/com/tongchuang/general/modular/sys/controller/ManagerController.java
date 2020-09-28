package com.tongchuang.general.modular.sys.controller;

import cn.hutool.core.io.resource.InputStreamResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.core.handler.FileHandler;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.main.model.dto.*;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeFileVO;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeFileService;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeService;
import com.tongchuang.general.modular.main.service.IUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "管理模块")
@RequestMapping("/sys/manager")
@RestController
public class ManagerController {

    @Autowired
    private IQuickResponseCodeService quickResponseCodeService;

    @Autowired
    private IQuickResponseCodeFileService quickResponseCodeFileService;

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "二维码列表")
    @GetMapping("/quick-response-code/list")
    public R<List<QuickResponseCodeVO>> quickResponseCodeList(QuickResponseCodeQueryDTO quickResponseCodeQueryDto) {
        return R.success().setData(quickResponseCodeService.quickResponseCodeList(quickResponseCodeQueryDto));
    }

    @ApiOperation(value = "二维码数量")
    @GetMapping("/quick-response-code/count")
    public R<List<QuickResponseCodeVO>> quickResponseCodeCount() {
        return R.success().setData(quickResponseCodeService.count());
    }

    @ApiOperation("二维码文件列表")
    @GetMapping("/quick-response-code/file")
    public R<List<QuickResponseCodeFileVO>> quickResponseCodeFileList(QuickResponseCodeFileQueryDTO quickResponseCodeFileQueryDto) {
        return R.success().setData(quickResponseCodeFileService.quickResponseCodeFileList(quickResponseCodeFileQueryDto));
    }

    @ApiOperation("二维码插入")
    @PostMapping("/quick-response-code")
    public R quickResponseCodeInsert(@RequestBody QuickResponseCodeFormDTO quickResponseCodeFormDTO) {
        if (quickResponseCodeService.quickResponseCodeInsert(quickResponseCodeFormDTO)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("二维码修改")
    @PutMapping("/quick-response-code")
    public R quickResponseCodeUpdate(@RequestBody QuickResponseCodeFormDTO quickResponseCodeFormDTO) {
        if (quickResponseCodeService.quickResponseCodeUpdate(quickResponseCodeFormDTO)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("二维码编辑")
    @PutMapping("/quick-response-code/edit")
    public R quickResponseCodeEdit(@RequestBody QuickResponseCodeEditDTO quickResponseCodeEditDto) {
        if (quickResponseCodeService.quickResponseCodeEdit(quickResponseCodeEditDto)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("二维码删除")
    @DeleteMapping("/quick-response-code/{id}")
    public R quickResponseCodeDelete(@PathVariable Long id) {
        if (quickResponseCodeService.quickResponseCodeDelete(id)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("二维码添加")
    @PostMapping("/quick-response-code/add")
    public R quickResponseCodeAdd(@RequestBody QuickResponseCodeAddDTO quickResponseCodeAddDto) {
        if (quickResponseCodeService.quickResponseCodeAdd(quickResponseCodeAddDto)) {
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("用户列表")
    @GetMapping("/user/list")
    public R<IPage<UserVO>> userList(UserQueryDTO userQuery){
        return R.success().setData(userService.userList(userQuery));
    }

    @ApiOperation("二维码授权")
    @PutMapping("/quick-response-code/authorize")
    public R quickResponseCodeAuthorize(@RequestBody QuickResponseCodeAuthorizeDTO quickResponseCodeAuthorizeDto) {
        if(quickResponseCodeService.quickResponseCodeAuthorize(quickResponseCodeAuthorizeDto)){
            return R.success();
        }
        return R.fail();
    }

    @ApiOperation("二维码授权取消")
    @PutMapping("/quick-response-code/authorize/cancel")
    public R quickResponseCodeAuthorizeCancel(@Param("二维码ID") Long id) {
        if(quickResponseCodeService.quickResponseCodeAuthorizeCancel(id)){
            return R.success();
        }
        return R.fail();
    }
}
