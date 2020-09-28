package com.tongchuang.general.modular.main.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("二维码视图")
@Data
public class QuickResponseCodeVO {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("二维码号")
    private String number;

    @ApiModelProperty("图片路径")
    private String imageFilePath;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("授权手机号")
    private String authorizePhone;

    @ApiModelProperty("文件数量")
    private String fileCount;

    @ApiModelProperty("不可更改数量")
    private String fileCountUpdateFalse;

    @ApiModelProperty("封面图片链接")
    private String coverImageLink;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
