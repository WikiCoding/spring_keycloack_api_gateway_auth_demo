package com.wikicoding.api_gateway.product;

import java.util.UUID;

public record ProductResponse(UUID id, String name, Integer quantity) {
}
