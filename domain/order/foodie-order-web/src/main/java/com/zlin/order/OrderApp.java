package com.zlin.order;

import com.zlin.item.service.ItemService;
import com.zlin.order.fallback.itemservice.ItemCommentsFeignClient;
import com.zlin.user.service.AddressService;
import com.zlin.user.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zlin
 * @date 20211007
 */
@SpringBootApplication
// 扫描mapper
@MapperScan(basePackages = "com.zlin.order.mapper")
// 默认扫描com.zlin包下的bean，需要添加扫描组件包org.n3r.idworker下的bean
@ComponentScan(basePackages = {"com.zlin", "org.n3r.idworker"})
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(
//        basePackages = {
//                "com.zlin.item.service",
//                "com.zlin.user.service"
//        }
        clients = {
                ItemCommentsFeignClient.class,
                ItemService.class,
                UserService.class,
                AddressService.class
        }
)
public class OrderApp {

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

}
