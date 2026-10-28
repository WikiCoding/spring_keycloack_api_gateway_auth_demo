package com.wikicoding.api_gateway.product;

import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsGatewayController {
    private final WebClient productsWebClient;

    public ProductsGatewayController(@Qualifier("productsWebClient") WebClient productsWebClient) {
        this.productsWebClient = productsWebClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> create(@RequestBody ProductRequest request) {
        return productsWebClient.post()
                .uri("/products")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }

    @GetMapping
    public Mono<ProductPageResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(ProductPageResponse.class);
    }
}
