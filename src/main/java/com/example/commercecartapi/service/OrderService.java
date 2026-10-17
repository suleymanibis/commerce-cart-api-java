package com.example.commercecartapi.service;

import com.example.commercecartapi.entity.*;
import com.example.commercecartapi.enums.OrderStatus;
import com.example.commercecartapi.exception.EmptyCartException;
import com.example.commercecartapi.exception.InsufficientStockException;
import com.example.commercecartapi.repository.OrderRepository;
import com.example.commercecartapi.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(String deliveryAddress) {
        Cart currentCart = cartService.getActiveCart();
        List<CartItem> cartItems = currentCart.getCartItems();
        BigDecimal totalPrice = calculateTotalPrice(currentCart);
        if (!cartItems.isEmpty()) {

            Order newOrder = Order.builder()
                    .user(currentCart.getUser())
                    .deliveryAddress(deliveryAddress)
                    .status(OrderStatus.PENDING)
                    .totalAmount(totalPrice)
                    .build();

            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                int requestedQuantity = cartItem.getQuantity();

                if (product.getStockQuantity() < requestedQuantity) {
                    throw new InsufficientStockException(product.getName() + " is out of stock! Remaining: " + product.getStockQuantity());
                }

                OrderItem orderItem = OrderItem.builder()
                        .order(newOrder)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getProduct().getPrice())
                        .build();

                newOrder.getOrderItems().add(orderItem);

                product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                if(product.getStockQuantity() == 0) {
                    product.changeStatusToOutOfStock();
                }
                productRepository.save(product);
            }

            Order savedOrder = orderRepository.save(newOrder);

            cartService.clearCart();
            return savedOrder;
        } else {
            throw new EmptyCartException("Cannot order with empty cart!");
        }
    }

    public List<Order> getUserOrders() {
        Long currentUserId = cartService.getActiveCart().getUser().getId();

        return orderRepository.findByUserId(currentUserId);
    }

    private BigDecimal calculateTotalPrice(Cart cart) {
        return cart.getCartItems().stream()
                .filter(Objects::nonNull)
                .map(CartItem::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
