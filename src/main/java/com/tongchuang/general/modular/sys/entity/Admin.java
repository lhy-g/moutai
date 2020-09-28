package com.tongchuang.general.modular.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("sys_admin")
@Data
public class Admin {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String nickname;

    private String username;

    private String password;

    private String phone;

    private Integer authStatus;

    private String lastLoginIp;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;
}
