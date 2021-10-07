package com.zlin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author zlin
 * @date 20201224
 */
@Configuration
public class CorsConfig {

    /**
     * 解决跨域问题
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加cors配置信息
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://192.168.3.16:8080");
        configuration.addAllowedOrigin("http://192.168.3.16");
        configuration.addAllowedOrigin("*");
        // 设置允许请求的方式
        configuration.addAllowedMethod("*");
        // 设置允许的header
        configuration.addAllowedHeader("*");
        // 是否发送cookie信息
        configuration.setAllowCredentials(true);

        // 2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", configuration);
        // 3. 返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }
}
