package com.tongchuang.general.modular.applet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("权益添加记录")
@Data
public class CodeAddRecordDTO {
	
	@ApiModelProperty(value = "类型ID")
	private String typeId;
	
	@ApiModelProperty(value = "生成人ID")
	private String userId;
	
	@ApiModelProperty(value = "添加个数")
	private String count;
	
	@ApiModelProperty(value = "起始编号")
	private Integer startNum;
	
	@ApiModelProperty(value = "结束编号")
	private Integer endNum;
	
	@ApiModelProperty(value = "时间戳")
	private String timeStamp;
	
	@ApiModelProperty(value = "生成人签字")
	private String sign;
	
	@ApiModelProperty(value = "状态")
	private String status;
	 
}
