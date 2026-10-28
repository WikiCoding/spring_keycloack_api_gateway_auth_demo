package com.wikicoding.products.services;

import com.wikicoding.products.controllers.ProductRequest;
import com.wikicoding.products.controllers.ProductPageResponse;
import com.wikicoding.products.controllers.ProductResponse;
import com.wikicoding.products.domain.Product;
import com.wikicoding.products.persistence.ProductRepository;
import java.util.Locale;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final R2dbcEntityTemplate entityTemplate;

    public Mono<ProductResponse> create(ProductRequest request) {
        Product product = new Product(
                UUID.randomUUID(),
                normalizeName(request.name()),
                request.quantity());
        return entityTemplate.insert(product).map(ProductResponse::from);
    }

    public Mono<ProductPageResponse> findPage(int page, int size) {
        if (page < 0) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must not be negative"));
        }

        int pageSize = Math.min(Math.max(size, 1), 100);
        long offset = (long) page * pageSize;

        return productRepository.findPage(pageSize + 1, offset)
                .map(ProductResponse::from)
                .collectList()
                .map(products -> new ProductPageResponse(
                        products.subList(0, Math.min(products.size(), pageSize)),
                        page,
                        pageSize,
                        products.size() > pageSize));
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Product name is required");
        }
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
