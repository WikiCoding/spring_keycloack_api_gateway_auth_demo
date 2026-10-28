package com.wikicoding.api_gateway.product;

import java.util.List;

public record ProductPageResponse(
        List<ProductResponse> items,
        int page,
        int size,
        boolean hasNext) {
}
