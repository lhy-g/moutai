package com.tongchuang.general.modular.applet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("通证")
@TableName("mt_equity_prove")
@Data
public class Prove {

	@ApiModelProperty(value = "Id", hidden = true)
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@ApiModelProperty(value = "权益Id", hidden = true)
	private String proveId;

	@ApiModelProperty("通证内容")
	private String content;

	@ApiModelProperty("发行地经度")
	private double lon;

	@ApiModelProperty("发行地纬度")
	private double lat;

	@ApiModelProperty(value = "确认签字", hidden = true)
	private String sign;

	@ApiModelProperty(value = "创建时间", hidden = true)
	private String createTime;

}
