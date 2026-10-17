package com.example.commercecartapi.repository;

import com.example.commercecartapi.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
