package com.example.commercecartapi.mapper;

import com.example.commercecartapi.dto.OrderItemResponse;
import com.example.commercecartapi.dto.OrderResponse;
import com.example.commercecartapi.entity.Order;
import com.example.commercecartapi.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toResponse(OrderItem orderItem);

}
