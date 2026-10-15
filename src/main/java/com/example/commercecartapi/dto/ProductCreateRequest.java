package com.example.commercecartapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {
    @NotBlank
    private String name;
    private String description;
    @Positive
    private BigDecimal price;
}
