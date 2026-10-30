package com.example.commercecartapi;

import com.example.commercecartapi.entity.Product;
import com.example.commercecartapi.enums.ProductStatus;
import com.example.commercecartapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findNonDeletedProducts_ShouldGetProductsOnlyActiveAndOutOfStock(){
        Product newProduct = new Product(
                "laptop",
                "you can develop projects",
                new BigDecimal("44999.95"),
                10);
        newProduct.changeStatusToDeleted();

        Product newProduct2 = new Product(
                "laptop 2",
                "you can develop projects",
                new BigDecimal("41999.95"),
                10);
        newProduct2.changeStatusToActive();

        Product newProduct3 = new Product(
                "laptop 3",
                "you can develop projects",
                new BigDecimal("34999.95"),
                10);
        newProduct3.changeStatusToOutOfStock();

        productRepository.saveAll(List.of(newProduct, newProduct2, newProduct3));

        List<Product> result = productRepository.findByStatusIsNot(ProductStatus.DELETED);

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(Product:: getName)
                .containsExactlyInAnyOrder("laptop 2", "laptop 3");

        assertThat(result)
                .extracting(Product:: getStatus)
                .containsExactlyInAnyOrder(ProductStatus.ACTIVE, ProductStatus.OUT_OF_STOCK);
    }
}
