package com.tongchuang.general.modular.main.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("二维码文件视图")
@Data
public class QuickResponseCodeFileVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("二维码ID")
    private Long quickResponseCodeId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("微视链接")
    private String weseeLink;

    @ApiModelProperty("微视视频链接")
    private String weseeVideoLink;

    @ApiModelProperty(value = "微视图片链接")
    private String weseeImageLink;

    @ApiModelProperty("更新状态(0-不可改 1-可改)")
    private Integer updateStatus;
}
