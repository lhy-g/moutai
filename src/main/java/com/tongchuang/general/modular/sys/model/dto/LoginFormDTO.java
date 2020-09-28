package com.tongchuang.general.modular.sys.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("登录表单")
@Data
public class LoginFormDTO {

    @ApiModelProperty("用户名")
    @NotNull
    private String username;

    @ApiModelProperty("密码")
    @NotNull
    private String password;
}
