package com.zlin;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @author zlin
 * @date 20211121
 */
@EnableHystrixDashboard
@SpringCloudApplication
public class HystrixDashboardApp {

    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboardApp.class, args);
    }
}
