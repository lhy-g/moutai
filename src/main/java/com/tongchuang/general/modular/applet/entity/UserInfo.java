package com.tongchuang.general.modular.applet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息")
@TableName("mt_user_info")
public class UserInfo {

	@ApiModelProperty(value = "主键",hidden = true)
	@TableId(value = "id_key", type = IdType.AUTO)
	private Long idKey;

	@ApiModelProperty(value = "用户unionId")
	private String unionId;
	@ApiModelProperty(value = "用户unionId(后8位)")
	private String userId;
	@ApiModelProperty(value = "用户昵称")
	private String nicename;
	@ApiModelProperty(value = "用户手机")
	private String phone;
	@ApiModelProperty(value = "用户头像Url")
	private String headUrl;
	@ApiModelProperty(value = "用户状态")
	private String status;
	@ApiModelProperty(value = "用户类型")
	private String type;
//	@ApiModelProperty(value = "用户公钥")
//	private String publicKey;
 

}
