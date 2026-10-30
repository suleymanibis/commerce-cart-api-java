package com.example.commercecartapi.entity;

import com.example.commercecartapi.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price; // We use big decimal because price is a money variable
    @Setter
    private int stockQuantity;

    @Enumerated(EnumType.STRING) // I used annotation @Enumerated(EnumType.String) because I want to sort it as a string in db.
    private ProductStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Product(String name, String description, BigDecimal price, int stockQuantity){
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = ProductStatus.ACTIVE;
    }

    public void changeStatusToDeleted(){
        this.status = ProductStatus.DELETED;
    }

    public void changeStatusToActive(){
        this.status = ProductStatus.ACTIVE;
    }

    public void changeStatusToOutOfStock(){
        this.status = ProductStatus.OUT_OF_STOCK;
    }
}
