package com.tongchuang.general.modular.applet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("二维码类型")
@Data
public class CodeTypeDTO {
	
	@ApiModelProperty(value = "二维码ID")
	private String typeId;
	
	@ApiModelProperty(value = "类型ID")
	private String typeName;
	
	@ApiModelProperty(value = "拥有用户ID")
	private String bossId;
	
	@ApiModelProperty(value = "时间戳")
	private String timeStamp;
	
	@ApiModelProperty(value = "图片")
	private String img;
	
	@ApiModelProperty(value = "拥有用户签字")
	private String sign;
	
	@ApiModelProperty(value = "状态")
	private String status;
	 
}
