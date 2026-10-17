package com.example.commercecartapi.service;

import com.example.commercecartapi.entity.Cart;
import com.example.commercecartapi.entity.CartItem;
import com.example.commercecartapi.entity.Order;
import com.example.commercecartapi.entity.OrderItem;
import com.example.commercecartapi.enums.OrderStatus;
import com.example.commercecartapi.exception.EmptyCartException;
import com.example.commercecartapi.repository.OrderRepository;
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
                OrderItem orderItem = OrderItem.builder()
                        .order(newOrder)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getProduct().getPrice())
                        .build();

                newOrder.getOrderItems().add(orderItem);
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
