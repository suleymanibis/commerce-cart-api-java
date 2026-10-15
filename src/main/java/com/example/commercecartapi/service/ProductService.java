package com.example.commercecartapi.service;

import com.example.commercecartapi.dto.ProductCreateRequest;
import com.example.commercecartapi.dto.ProductResponse;
import com.example.commercecartapi.entity.Product;
import com.example.commercecartapi.enums.ProductStatus;
import com.example.commercecartapi.exception.ResourceNotFoundException;
import com.example.commercecartapi.mapper.ProductMapper;
import com.example.commercecartapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    public ProductResponse addNewProduct(ProductCreateRequest request){
        Product newProduct = productMapper.toEntity(request);
        newProduct.changeStatusToActive();

        Product savedProduct = productRepository.save(newProduct);
        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse deleteProduct(Long id){
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot found product with ID " + id));

        existingProduct.changeStatusToDeleted();
        Product savedProduct = productRepository.save(existingProduct);

        return productMapper.toResponse(savedProduct);
    }

    public List<ProductResponse> getAllNonDeletedProducts(){
        return productRepository.findByStatusIsNot(ProductStatus.DELETED)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
