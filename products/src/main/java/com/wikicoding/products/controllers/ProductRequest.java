package com.wikicoding.products.controllers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank(message = "Product name is required") String name,
        @NotNull(message = "Product quantity is required")
        @Min(value = 1, message = "Product quantity must be greater than 0") Integer quantity) {
}
