package com.example.commercecartapi.dto;

import com.example.commercecartapi.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private ProductStatus status;
}
