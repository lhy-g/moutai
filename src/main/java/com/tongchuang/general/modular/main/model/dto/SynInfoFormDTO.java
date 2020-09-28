package com.tongchuang.general.modular.main.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("同步信息表单")
@Data
public class SynInfoFormDTO {

    @ApiModelProperty("头像链接")
    private String headPortraitLink;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("回话键")
    private String sessionKey;
    
    @ApiModelProperty("用户unionId")
    private String unionId;

    @ApiModelProperty("加密算法的初始向量")
    private String iv;

    @ApiModelProperty("敏感数据对应的云 ID")
    private String encryptedData;
}
