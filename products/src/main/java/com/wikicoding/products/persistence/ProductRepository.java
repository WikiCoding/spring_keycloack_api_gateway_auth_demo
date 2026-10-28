package com.wikicoding.products.persistence;

import com.wikicoding.products.domain.Product;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, UUID> {
    @Query("SELECT id, name, quantity FROM products ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<Product> findPage(@Param("limit") int limit, @Param("offset") long offset);
}
