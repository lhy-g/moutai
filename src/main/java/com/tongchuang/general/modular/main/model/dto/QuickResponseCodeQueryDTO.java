package com.tongchuang.general.modular.main.model.dto;

import com.tongchuang.general.core.constant.PageQuery;
import com.tongchuang.general.core.constant.TimeFrameQuery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("二维码文件查询对象")
@Data
public class QuickResponseCodeQueryDTO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty(value = "授权用户ID", hidden = true)
    private Long authorizeUserId;

    @ApiModelProperty("时间范围")
    private TimeFrameQuery timeFrame;

    @ApiModelProperty("分页")
    private PageQuery page = new PageQuery();
}
