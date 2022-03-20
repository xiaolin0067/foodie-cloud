package com.zlin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zlin
 * @date 20220320
 */
@EnableAdminServer
@SpringBootApplication
public class SpringBootAdminApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminApp.class, args);
    }

}
