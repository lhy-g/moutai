package com.tongchuang.general.modular.applet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("二维码视频")
@Data
public class CodeVideoDTO {
	
	@ApiModelProperty("二维码ID/销售劵ID")
	private String codeId;
	
	@ApiModelProperty("二维码名称/销售劵名称")
	private String codeName;
	
	@ApiModelProperty("微视链接")
	private String weseeLink;
	
	@ApiModelProperty("视频名称")
	private String videoName;
	
	@ApiModelProperty("视频链接地址")
	private String videoUrl;
	
	@ApiModelProperty("图片链接地址")
	private String imageUrl;
	
	@ApiModelProperty("排序")
	private String sort;
}
