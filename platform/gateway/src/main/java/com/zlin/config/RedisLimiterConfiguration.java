package com.zlin.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * @author zlpang
 * @date 20211201
 */
@Configuration
public class RedisLimiterConfiguration {

    @Bean
    @Primary
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    @Bean
    @Primary
    public RedisRateLimiter redisRateLimiterUser() {
        return new RedisRateLimiter(10,20);
    }

    @Bean
    public RedisRateLimiter redisRateLimiterItem() {
        return new RedisRateLimiter(20,50);
    }

}
