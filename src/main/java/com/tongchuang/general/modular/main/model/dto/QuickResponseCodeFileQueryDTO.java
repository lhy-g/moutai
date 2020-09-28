package com.tongchuang.general.modular.main.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("二维码文件查询对象")
@Data
public class QuickResponseCodeFileQueryDTO {

    @ApiModelProperty("二维码ID")
    private Long quickResponseCodeId;

    @ApiModelProperty("二维码号码")
    private String quickResponseCodeNumber;
}
