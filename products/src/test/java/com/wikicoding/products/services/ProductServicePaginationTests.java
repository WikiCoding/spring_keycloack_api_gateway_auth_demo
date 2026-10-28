package com.wikicoding.products.services;

import com.wikicoding.products.domain.Product;
import com.wikicoding.products.persistence.ProductRepository;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServicePaginationTests {
    @ParameterizedTest
    @CsvSource({"0,0,false", "9,9,false", "10,10,false", "11,10,true"})
    void returnsOnlyRequestedItemsAndDetectsNextPage(int rows, int expectedItems, boolean hasNext) {
        ProductRepository repository = mock(ProductRepository.class);
        ProductService service = new ProductService(repository, mock(R2dbcEntityTemplate.class));
        var products = IntStream.range(0, rows)
                .mapToObj(i -> new Product(UUID.randomUUID(), "product-" + i, i))
                .toList();
        when(repository.findPage(11, 20L)).thenReturn(Flux.fromIterable(products));

        StepVerifier.create(service.findPage(2, 10))
                .assertNext(response -> {
                    assertEquals(expectedItems, response.items().size());
                    assertEquals(hasNext, response.hasNext());
                    assertEquals(2, response.page());
                    assertEquals(10, response.size());
                    assertEquals(products.stream().limit(10).map(Product::id).toList(),
                            response.items().stream().map(item -> item.id()).toList());
                })
                .verifyComplete();
        verify(repository).findPage(11, 20L);
        verifyNoMoreInteractions(repository);
    }
}
