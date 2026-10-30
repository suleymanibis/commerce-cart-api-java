package com.example.commercecartapi.controller;

import com.example.commercecartapi.dto.CreateOrderRequest;
import com.example.commercecartapi.dto.OrderResponse;
import com.example.commercecartapi.entity.Order;
import com.example.commercecartapi.mapper.OrderMapper;
import com.example.commercecartapi.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request){
        Order newOrder = orderService.createOrder(request.getDeliveryAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(newOrder));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(){
        List<Order> orders = orderService.getUserOrders();
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order: orders){
            orderResponses.add(orderMapper.toResponse(order));
        }

        return ResponseEntity.ok(orderResponses);
    }
}
