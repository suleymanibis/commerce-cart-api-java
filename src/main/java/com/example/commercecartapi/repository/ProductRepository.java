package com.example.commercecartapi.repository;

import com.example.commercecartapi.entity.Product;
import com.example.commercecartapi.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusIsNot(ProductStatus status);
}
