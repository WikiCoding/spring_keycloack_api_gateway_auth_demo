package com.wikicoding.products.controllers;

import com.wikicoding.products.domain.Product;
import java.util.UUID;

public record ProductResponse(UUID id, String name, Integer quantity) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.id(), product.name(), product.quantity());
    }
}
