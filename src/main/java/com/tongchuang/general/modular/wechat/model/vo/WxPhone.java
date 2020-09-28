package com.tongchuang.general.modular.wechat.model.vo;

import lombok.Data;

@Data
public class WxPhone {

    private String phoneNumber;

    private String purePhoneNumber;

    private String countryCode;

    private Watermark watermark;
}
