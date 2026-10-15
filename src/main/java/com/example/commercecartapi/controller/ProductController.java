package com.example.commercecartapi.controller;

import com.example.commercecartapi.dto.ProductCreateRequest;
import com.example.commercecartapi.dto.ProductResponse;
import com.example.commercecartapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ProductResponse> addNewProduct(@Valid @RequestBody ProductCreateRequest request){
        ProductResponse productResponse = productService.addNewProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAllNonDeletedProducts(){
        List<ProductResponse> productResponses = productService.getAllNonDeletedProducts();

        return ResponseEntity.ok(productResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Long id){
        ProductResponse productResponse = productService.deleteProduct(id);

        return ResponseEntity.ok(productResponse);
    }
}
