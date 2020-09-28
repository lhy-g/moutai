package com.tongchuang.general.modular.applet.constant;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * 
 * @author Admin
 *
 */

@Data
@ApiModel("简易证书")
public class SimpleCert implements Serializable {
	
	
	@ApiModelProperty(value="书名称")
	private static final String CERT_NAME = "n";
	
	@ApiModelProperty(value="证书内容")
	private static final String CONTENT = "s";
	
	@ApiModelProperty(value="签名")
	private static final String SIGN = "s";
	
	
//	@ApiModelProperty(value="签名")
//	private static final String SIGN = "s";
//	
//	
//	@ApiModelProperty(value="签名")
//	private static final String SIGN = "s";
//	
	
	@ApiModelProperty(value = "证书名称")
	private String n;
	
	@ApiModelProperty(value = "证书内容")
	private String c;
	
	@ApiModelProperty(value = "时间戳（发行时间）")
	private String d;
	
	@ApiModelProperty(value = "老板UnionID")
	private String b;
	
	@ApiModelProperty(value = "时间戳（代理人发行时间）")
	private String t;
	
	@ApiModelProperty(value = "申请人UnionID")
	private String a;
	
	@ApiModelProperty(value = "申请人签名")
	private String s;
}
