package com.tongchuang.general.modular.sys.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("登录视图")
@Builder
@Data
public class LoginVO {

    @ApiModelProperty("凭证")
    private String token;
}
