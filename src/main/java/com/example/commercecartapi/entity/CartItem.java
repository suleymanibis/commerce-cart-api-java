package com.example.commercecartapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    private Product product;

    private int quantity;

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return new BigDecimal(String.valueOf(product.getPrice())).multiply(BigDecimal.valueOf(quantity));
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }
}
