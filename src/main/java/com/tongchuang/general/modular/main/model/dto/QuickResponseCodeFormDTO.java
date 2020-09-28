package com.tongchuang.general.modular.main.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("二维码表单")
@Data
public class QuickResponseCodeFormDTO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("二维码号")
    private String number;

    @ApiModelProperty("图片路径")
    private String imageFilePath;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty(value = "授权用户ID", hidden = true)
    private Long authorizeUserId;
}
