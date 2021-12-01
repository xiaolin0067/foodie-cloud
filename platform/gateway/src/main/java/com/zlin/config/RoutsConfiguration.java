package com.zlin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @author zlpang
 * @date 20211201
 */
@Configuration
public class RoutsConfiguration {

    @Autowired
    private KeyResolver hostNameKeyResolver;

//    @Qualifier("redisRateLimiterUser")
    @Autowired
    private RedisRateLimiter redisRateLimiterUser;

    @Autowired
    private RedisRateLimiter redisRateLimiterItem;

    /**
     * 配置路由规则
     */
    @Bean
    public RouteLocator routs(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(
                        r -> r.path("/passport/**", "/address/**", "/userInfo/**", "/center/**")
                                .filters(f -> f.requestRateLimiter(c -> {
                                    c.setKeyResolver(hostNameKeyResolver);
                                    c.setRateLimiter(redisRateLimiterUser);
//                                    c.setStatusCode(HttpStatus.BAD_GATEWAY);
                                }))
                                .uri("lb://FOODIE-USER-SERVICE")
                )
                .route(
                        r -> r.path("/items/**")
                                .filters(f -> f.requestRateLimiter(c -> {
                                    c.setKeyResolver(hostNameKeyResolver);
                                    c.setRateLimiter(redisRateLimiterItem);
                                }))
                                .uri("lb://FOODIE-ITEM-SERVICE")
                )
                .route(
                        r -> r.path("/orders/**", "/myorders/**", "/mycomments/**")
                                .uri("lb://FOODIE-ORDER-SERVICE")
                )
                .route(
                        r -> r.path("/shopcart/**")
                                .uri("lb://FOODIE-CART-SERVICE")
                )
//                .route(
//                        r -> r.path("/search/**", "/index/**", "/items/search", "/items/catItems")
//                                .uri("lb://FOODIE-SEARCH-SERVICE")
//                )
                .build();
    }

}
