package com.tongchuang.general.modular.sys.controller;

import cn.hutool.core.io.resource.InputStreamResource;

import com.tongchuang.general.core.annotation.OpenMapping;
import com.tongchuang.general.core.constant.FileBody;
import com.tongchuang.general.core.handler.FileHandler;
import com.tongchuang.general.core.web.responce.R;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(tags = "管理员文件接口")
@RestController("/sys/file")
public class SysFileControler {

    @Autowired
    private FileHandler fileHandler;


    @OpenMapping
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R<FileBody> upload(@ApiParam("文件") MultipartFile file) throws IOException {
        InputStreamResource isr = new InputStreamResource(file.getInputStream(), file.getOriginalFilename());
        FileBody fileBody = fileHandler.upload(isr);
        if(fileBody != null){
            return R.success().setData(fileBody);
        }
        return R.fail();
    }
}
