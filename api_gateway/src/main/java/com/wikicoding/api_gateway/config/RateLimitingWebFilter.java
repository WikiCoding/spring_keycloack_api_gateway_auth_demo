package com.wikicoding.api_gateway.config;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RateLimitingWebFilter implements WebFilter {
    private static final int REQUESTS_PER_SECOND = 3;
    private static final Duration WINDOW = Duration.ofSeconds(1);
    private static final String KEY_PREFIX = "api-gateway:rate-limit:";

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>("""
            local current = redis.call('TIME')
            local now = tonumber(current[1]) * 1000 + math.floor(tonumber(current[2]) / 1000)
            local windowStart = tonumber(redis.call('HGET', KEYS[1], 'windowStart'))
            local count = tonumber(redis.call('HGET', KEYS[1], 'count')) or 0

            if not windowStart or now - windowStart >= tonumber(ARGV[1]) then
              windowStart = now
              count = 0
            end

            if count >= tonumber(ARGV[2]) then
              redis.call('HSET', KEYS[1], 'windowStart', windowStart, 'count', count)
              redis.call('PEXPIRE', KEYS[1], tonumber(ARGV[1]))
              return 0
            end

            count = count + 1
            redis.call('HSET', KEYS[1], 'windowStart', windowStart, 'count', count)
            redis.call('PEXPIRE', KEYS[1], tonumber(ARGV[1]))
            return 1
            """, Long.class);

    private final ReactiveStringRedisTemplate redis;

    public RateLimitingWebFilter(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
                .map(principal -> "user:" + principal.getName())
                .switchIfEmpty(Mono.fromSupplier(() -> "ip:" + clientIp(exchange)))
                .flatMap(client -> isAllowed(client)
                        .flatMap(allowed -> {
                            if (allowed) {
                                return chain.filter(exchange);
                            }
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            exchange.getResponse().getHeaders().set("Retry-After", "1");
                            return exchange.getResponse().setComplete();
                        }));
    }

    private Mono<Boolean> isAllowed(String client) {
        return redis.execute(
                        RATE_LIMIT_SCRIPT,
                        List.of(KEY_PREFIX + client),
                        List.of(
                                String.valueOf(WINDOW.toMillis()),
                                String.valueOf(REQUESTS_PER_SECOND)))
                .next()
                .map(result -> result == 1L);
    }

    private String clientIp(ServerWebExchange exchange) {
        InetSocketAddress address = exchange.getRequest().getRemoteAddress();
        return address == null ? "unknown" : address.getAddress().getHostAddress();
    }
}
