package com.example.commercecartapi.mapper;

import com.example.commercecartapi.dto.ProductCreateRequest;
import com.example.commercecartapi.dto.ProductResponse;
import com.example.commercecartapi.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductCreateRequest productCreateRequest);
    ProductResponse toResponse(Product product);
}
