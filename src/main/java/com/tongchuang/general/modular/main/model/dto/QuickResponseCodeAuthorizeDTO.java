package com.tongchuang.general.modular.main.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("二维码授权")
@Data
public class QuickResponseCodeAuthorizeDTO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("手机号")
    private String phone;
}
