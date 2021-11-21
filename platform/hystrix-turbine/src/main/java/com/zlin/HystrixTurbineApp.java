package com.zlin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * @author zlin
 * @date 20211107
 */
@EnableDiscoveryClient
@EnableTurbine
@EnableAutoConfiguration
public class HystrixTurbineApp {

    public static void main(String[] args) {
        SpringApplication.run(HystrixTurbineApp.class, args);
    }

}
