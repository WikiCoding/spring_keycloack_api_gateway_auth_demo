package com.wikicoding.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.Principal;

@Configuration
public class GatewayRoutes {
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal().map(Principal::getName);
    }

    @Bean
    RedisRateLimiter productsRateLimiter(
            @Value("${gateway.rate-limit.replenish-rate}") int replenishRate,
            @Value("${gateway.rate-limit.burst-capacity}") int burstCapacity) {
        return new RedisRateLimiter(replenishRate, burstCapacity);
    }

    @Bean
    RouteLocator productRoutes(RouteLocatorBuilder builder,
            @Value("${products.service.url}") String productsUri,
            RedisRateLimiter productsRateLimiter, KeyResolver userKeyResolver) {
        return builder.routes()
                .route("products", route -> route.path("/api/v1/products")
                        .and().method("GET", "POST")
                        .filters(filters -> filters.setPath("/products")
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(productsRateLimiter)
                                        .setKeyResolver(userKeyResolver)))
                        .uri(productsUri))
                .build();
    }
}
