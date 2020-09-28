package com.tongchuang.general.modular.applet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("公司信息")
@Data
public class CompanyInfoDTO {

	@ApiModelProperty(value = "企业ID")
	private String companyId;

	@ApiModelProperty(value = "企业名称")
	private String companyName;
	
	@ApiModelProperty(value = "bossId", hidden = true)
	private String bossId;
	
	@ApiModelProperty(value = "公司类型(1:权益生成;2:运营传播;3:社交演绎)")
	private String type;

	@ApiModelProperty(value = "法人名称")
	private String legalPerson;

	@ApiModelProperty(value = "统一社会信用代码(Unified social credit code)")
	private String uscc;
 
	@ApiModelProperty(value = "执照" )
	private String licenseImg;
	
	@ApiModelProperty(value = "企业照片" )
	private String img;

	@ApiModelProperty(value = "公司状态", hidden = true)
	private String status;

}
