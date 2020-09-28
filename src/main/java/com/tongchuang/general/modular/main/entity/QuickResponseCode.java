package com.tongchuang.general.modular.main.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("main_quick_response_code")
@Data
public class QuickResponseCode {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String number;

    private String imageFilePath;

    private String name;

    private Long authorizeUserId;

    private LocalDateTime createTime;
}
