package com.tongchuang.general.modular.main.model.dto;

import com.tongchuang.general.core.constant.PageQuery;
import com.tongchuang.general.core.constant.TimeFrameQuery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户查询对象")
@Data
public class UserQueryDTO {

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("时间范围")
    private TimeFrameQuery timeFrame;

    @ApiModelProperty("分页")
    private PageQuery page = new PageQuery();
}
