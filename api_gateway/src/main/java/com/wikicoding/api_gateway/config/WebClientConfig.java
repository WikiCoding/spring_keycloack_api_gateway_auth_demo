package com.wikicoding.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    WebClient productsWebClient(@Value("${products.service.url}") String productsServiceUrl) {
        return WebClient.builder().baseUrl(productsServiceUrl).build();
    }

    @Bean
    WebClient keycloakWebClient(@Value("${keycloak.url}") String keycloakUrl) {
        return WebClient.builder().baseUrl(keycloakUrl).build();
    }
}
