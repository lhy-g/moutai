package com.tongchuang.general.modular.applet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("二维码信息")
@Data
public class CodeInfoDTO {
	
	@ApiModelProperty(value = "类型ID")
	private String typeId;
	
	@ApiModelProperty(value = "二维码ID")
	private String codeId;
	
	@ApiModelProperty(value = "老板ID")
	private String bossId;
	
	@ApiModelProperty(value = "生成人ID")
	private String userId;
	
	@ApiModelProperty(value = "生成人签字")
	private String sign;
	
	@ApiModelProperty(value = "编号")
	private Integer number;
	
	@ApiModelProperty(value = "状态")
	private String status;
	 
}
