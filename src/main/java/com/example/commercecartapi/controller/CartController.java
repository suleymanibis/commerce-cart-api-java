package com.example.commercecartapi.controller;

import com.example.commercecartapi.dto.AddToCartRequest;
import com.example.commercecartapi.dto.CartResponse;
import com.example.commercecartapi.entity.Cart;
import com.example.commercecartapi.entity.CartItem;
import com.example.commercecartapi.mapper.CartMapper;
import com.example.commercecartapi.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartMapper cartMapper;
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCurrentCart(){
        Cart currentCart = cartService.getActiveCart();
        return ResponseEntity.ok(cartMapper.toResponse(currentCart));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request){
        Cart updatedCart = cartService.addProductToCart(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cartMapper.toResponse(updatedCart));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeProductFromCart(@PathVariable Long productId){
        Cart updatedCart = cartService.removeProductFromCart(productId);
        return ResponseEntity.ok(cartMapper.toResponse(updatedCart));
    }

    @DeleteMapping()
    public ResponseEntity<CartResponse> clearCart(){
        Cart updatedCart = cartService.clearCart();
        return ResponseEntity.ok(cartMapper.toResponse(updatedCart));
    }
}
