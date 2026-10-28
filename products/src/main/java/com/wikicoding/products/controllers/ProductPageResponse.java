package com.wikicoding.products.controllers;

import java.util.List;

public record ProductPageResponse(
        List<ProductResponse> items,
        int page,
        int size,
        boolean hasNext) {
}
