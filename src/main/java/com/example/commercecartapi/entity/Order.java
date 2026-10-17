package com.example.commercecartapi.entity;

import com.example.commercecartapi.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private String deliveryAddress;
    private OrderStatus status;
    private BigDecimal totalAmount;
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    public Order(User user, List<OrderItem> orderItems, String deliveryAddress, OrderStatus status, BigDecimal totalAmount){
        this.user = user;
        this.orderItems = orderItems;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
    }
}
