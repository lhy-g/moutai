package com.tongchuang.general.modular.main.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("main_quick_response_code_file")
@Data
public class QuickResponseCodeFile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long quickResponseCodeId;

    private String name;

    private String weseeLink;

    private String weseeVideoLink;

    private String weseeImageLink;

    private Integer updateStatus;
}
