package com.example.commercecartapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(User user, List<CartItem> cartItems){
        this.user = user;
        this.cartItems = cartItems;
    }
}
