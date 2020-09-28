package com.tongchuang.general.modular.main.controller;

import com.tongchuang.general.core.annotation.OpenMapping;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.core.web.controller.BaseController;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.main.model.dto.*;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeFileVO;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeVO;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeFileService;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api(tags = "二维码接口")
@RequestMapping("/main/quick-response-code")
@RestController
public class QuickResponseCodeController extends BaseController {

    @Autowired
    private IQuickResponseCodeService quickResponseCodeService;

    @Autowired
    private IQuickResponseCodeFileService quickResponseCodeFileService;

    @ApiOperation(value = "二维码列表")
    @GetMapping("/list")
    public R<List<QuickResponseCodeVO>> quickResponseCodeList(QuickResponseCodeQueryDTO quickResponseCodeQueryDto) {
        quickResponseCodeQueryDto.setAuthorizeUserId(UserContextHolder.get().getId());
        return R.success().setData(quickResponseCodeService.quickResponseCodeList(quickResponseCodeQueryDto));
    }

    @OpenMapping
    @ApiOperation("二维码文件列表")
    @GetMapping("/file/list")
    public R<List<QuickResponseCodeFileVO>> quickResponseCodeFileList(QuickResponseCodeFileQueryDTO quickResponseCodeFileQueryDto) {
        return R.success().setData(quickResponseCodeFileService.quickResponseCodeFileList(quickResponseCodeFileQueryDto));
    }

    @ApiOperation("批量更新文件")
    @PutMapping("/file/batch")
    public R<List<QuickResponseCodeFileVO>> quickResponseCodeFileUpdateBatch(@RequestBody List<QuickResponseCodeFileFormDTO> quickResponseCodeFileFormDtos) throws IOException {
        return R.success().setData(quickResponseCodeFileService.quickResponseCodeFileUpdateBatch(quickResponseCodeFileFormDtos));
    }

    @OpenMapping
    @ApiOperation(value = "微视解析")
    @PostMapping("/wesee/link/analysis")
    public R<List<WeseeLink>> weseeAnalysis(@RequestBody List<String> weseeLink){
        List<WeseeLink> weseeLinks = quickResponseCodeFileService.weseeAnalysis(weseeLink);
        if(weseeLinks != null){
            return R.success().setData(weseeLinks);
        }
        return R.fail();
    }
}
