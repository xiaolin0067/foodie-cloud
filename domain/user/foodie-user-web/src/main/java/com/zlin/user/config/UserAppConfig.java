package com.zlin.user.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author zlin
 * @date 20211128
 */
@Configuration
@RefreshScope
@Data
public class UserAppConfig {

    @Value("${userservice.registration.disabled:false}")
    private boolean disableRegistration;

}
