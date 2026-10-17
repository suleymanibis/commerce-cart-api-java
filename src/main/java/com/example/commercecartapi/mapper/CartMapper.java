package com.example.commercecartapi.mapper;

import com.example.commercecartapi.dto.AddToCartRequest;
import com.example.commercecartapi.dto.CartItemResponse;
import com.example.commercecartapi.dto.CartResponse;
import com.example.commercecartapi.entity.Cart;
import com.example.commercecartapi.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "cartTotalPrice", expression = "java(calculateTotalPrice(cart))")
    @Mapping(source = "id", target = "cartId")
    CartResponse toResponse(Cart cart);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "unitPrice")
    CartItemResponse toResponse(CartItem cartItem);

    default BigDecimal calculateTotalPrice(Cart cart) {
        return cart.getCartItems().stream()
                .filter(Objects::nonNull)
                .map(CartItem::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
