package com.tongchuang.general.modular.applet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("二维码")
@TableName("main_quick_response_code")
@Data
public class RqCode {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableId(value = "id", type = IdType.AUTO)
    private String codeId;

    @ApiModelProperty("二维码编号")
    private String number;

    @ApiModelProperty("二维码图片文件url")
    private String imageUrl;

    @ApiModelProperty("二维码名称")
    private String name;

    @ApiModelProperty("二维码归属用户ID")
    private String userId;
    
    @ApiModelProperty("二维码创建时间")
    private LocalDateTime createTime;
    
//    @ApiModelProperty("二维码状态")
//    private String status;
//    
//    @ApiModelProperty("二维码类型")
//    private String type;
}
