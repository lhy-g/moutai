package com.tongchuang.general.modular.main.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("二维码添加对象")
@Data
public class QuickResponseCodeAddDTO {

    @ApiModelProperty("二维码表单")
    private QuickResponseCodeFormDTO quickResponseCodeFormDto;

    @ApiModelProperty("二维码文件表单")
    private List<QuickResponseCodeFileFormDTO> quickResponseCodeFileFormDtos;
}
