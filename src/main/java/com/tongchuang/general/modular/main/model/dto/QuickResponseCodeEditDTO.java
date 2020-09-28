package com.tongchuang.general.modular.main.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("二维码编辑")
@Data
public class QuickResponseCodeEditDTO {

    @ApiModelProperty("二维码表单")
    private QuickResponseCodeFormDTO quickResponseCodeFormDto;

    @ApiModelProperty("二维码文件表单")
    private List<QuickResponseCodeFileFormDTO> quickResponseCodeFileFormDtos;
}
