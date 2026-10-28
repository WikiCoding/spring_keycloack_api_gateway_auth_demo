package com.wikicoding.api_gateway;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "eureka.client.enabled=false", "spring.cloud.loadbalancer.cache.enabled=false"
})
class ApiGatewayApplicationTests {
    private static final Set<String> visited = ConcurrentHashMap.newKeySet();
    private static final DisposableServer first = backend("first");
    private static final DisposableServer second = backend("second");

    private static DisposableServer backend(String name) {
        return HttpServer.create().host("127.0.0.1").port(0).handle((request, response) -> {
            visited.add(name);
            if (request.uri().contains("/protocol/openid-connect/token")) {
                return response.header("Content-Type", "application/json")
                        .sendString(Mono.just("{\"access_token\":\"test-token\",\"token_type\":\"Bearer\",\"expires_in\":300,\"refresh_expires_in\":1800,\"refresh_token\":\"refresh\"}"));
            }
            if (request.method().name().equals("POST")) {
                return response.status(201).header("Content-Type", "application/json")
                        .send(request.receive().retain());
            }
            return response.header("Content-Type", "text/plain").sendString(Mono.just(request.uri()));
        }).bindNow();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("products.service.url", () -> "lb://products");
        registry.add("spring.cloud.discovery.client.simple.instances.products[0].uri",
                () -> "http://127.0.0.1:" + first.port());
        registry.add("spring.cloud.discovery.client.simple.instances.products[1].uri",
                () -> "http://127.0.0.1:" + second.port());
        registry.add("keycloak.url", () -> "http://127.0.0.1:" + first.port());
    }

    @LocalServerPort int port;
    @MockitoBean ReactiveJwtDecoder jwtDecoder;
    @MockitoBean RedisRateLimiter productsRateLimiter;
    private WebTestClient client;

    @BeforeEach
    void setup() {
        visited.clear();
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        when(jwtDecoder.decode(anyString())).thenAnswer(invocation -> Mono.just(Jwt
                .withTokenValue(invocation.getArgument(0)).header("alg", "RS256")
                .subject("user-123").issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build()));
        when(productsRateLimiter.isAllowed(anyString(), anyString()))
                .thenReturn(Mono.just(new RateLimiter.Response(true, Map.of())));
    }

    @Test
    void productRequestsRequireAuthentication() {
        client.get().uri("/api/v1/products").exchange().expectStatus().isUnauthorized();
        assertThat(visited).isEmpty();
    }

    @Test
    void rewritesPathPreservesPaginationAndBalancesAcrossInstances() {
        for (int i = 0; i < 4; i++) {
            client.get().uri("/api/v1/products?page=2&size=5").headers(h -> h.setBearerAuth("token"))
                    .exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("/products?page=2&size=5");
        }
        assertThat(visited).containsExactlyInAnyOrder("first", "second");
    }

    @Test
    void forwardsCreateBodyAndCreatedStatus() {
        client.post().uri("/api/v1/products").headers(h -> h.setBearerAuth("token"))
                .header("Content-Type", "application/json").bodyValue("{\"name\":\"phone\",\"quantity\":3}")
                .exchange().expectStatus().isCreated().expectBody().jsonPath("$.quantity").isEqualTo(3);
        verify(productsRateLimiter).isAllowed("products", "user-123");
    }

    @Test
    void rejectsExceededLimitBeforeCallingProducts() {
        when(productsRateLimiter.isAllowed("products", "user-123"))
                .thenReturn(Mono.just(new RateLimiter.Response(false, Map.of())));
        client.get().uri("/api/v1/products").headers(h -> h.setBearerAuth("token"))
                .exchange().expectStatus().isEqualTo(429);
        assertThat(visited).isEmpty();
    }

    @Test
    void loginRemainsPublic() {
        client.post().uri("/api/v1/auth/login").header("Content-Type", "application/json")
                .bodyValue("{\"username\":\"developer\",\"password\":\"developer\"}")
                .exchange().expectStatus().isOk().expectBody().jsonPath("$.access_token").isEqualTo("test-token");
    }

    @Test
    void preflightRemainsPublic() {
        client.options().uri("/api/v1/products").header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type")
                .exchange().expectStatus().isOk().expectHeader().exists("Access-Control-Allow-Origin");
    }

    @Test
    void doesNotExposeOtherProductPathsOrMethods() {
        client.delete().uri("/api/v1/products").headers(h -> h.setBearerAuth("token"))
                .exchange().expectStatus().isNotFound();
        client.get().uri("/api/v1/products/private").headers(h -> h.setBearerAuth("token"))
                .exchange().expectStatus().isNotFound();
        assertThat(visited).isEmpty();
    }

    @AfterAll
    static void stopBackends() {
        first.disposeNow();
        second.disposeNow();
    }
}
