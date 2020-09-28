package com.tongchuang.general.modular.sys.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户修改密码")
@Data
public class AdminUpdatePasswordDTO {

    @ApiModelProperty("原密码")
    private String oldPassword;

    @ApiModelProperty("新密码")
    private String newPassword;
}
