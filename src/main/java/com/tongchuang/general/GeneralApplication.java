package com.tongchuang.general;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneralApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneralApplication.class, args);
        System.out.println("GeneralApplication 启动成功!!");
    }

}
