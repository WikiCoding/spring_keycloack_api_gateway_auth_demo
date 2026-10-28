package com.wikicoding.api_gateway.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final WebClient keycloakWebClient;
    private final String realm;
    private final String clientId;

    public AuthenticationController(
            @Qualifier("keycloakWebClient") WebClient keycloakWebClient,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId) {
        this.keycloakWebClient = keycloakWebClient;
        this.realm = realm;
        this.clientId = clientId;
    }

    @PostMapping("/login")
    public Mono<KeycloakTokenResponse> login(@RequestBody LoginRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", request.username());
        form.add("password", request.password());

        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class);
    }
}
