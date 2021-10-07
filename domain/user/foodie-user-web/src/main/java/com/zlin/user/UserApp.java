package com.zlin.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zlin
 * @date 20211007
 */
@SpringBootApplication
// 扫描mapper
@MapperScan(basePackages = "com.zlin.user.mapper")
// 默认扫描com.zlin包下的bean，需要添加扫描组件包org.n3r.idworker下的bean
@ComponentScan(basePackages = {"com.zlin", "org.n3r.idworker"})
@EnableDiscoveryClient
public class UserApp {

    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }

}
