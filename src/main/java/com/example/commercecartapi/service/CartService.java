package com.example.commercecartapi.service;

import com.example.commercecartapi.entity.Cart;
import com.example.commercecartapi.entity.CartItem;
import com.example.commercecartapi.entity.Product;
import com.example.commercecartapi.entity.User;
import com.example.commercecartapi.enums.ProductStatus;
import com.example.commercecartapi.exception.InsufficientStockException;
import com.example.commercecartapi.exception.NotActiveProductException;
import com.example.commercecartapi.exception.ResourceNotFoundException;
import com.example.commercecartapi.repository.CartRepository;
import com.example.commercecartapi.repository.ProductRepository;
import com.example.commercecartapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public Cart getActiveCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User cannot found!"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                   Cart newCart = new Cart(user, new ArrayList<>());
                   return cartRepository.save(newCart);
                });
    }

    public Cart addProductToCart(Long productId, int quantity) {
        Cart currentCart = getActiveCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " cannot found!"));

        if (product.getStatus() == ProductStatus.ACTIVE) {
            List<CartItem> cartItems = currentCart.getCartItems();
            CartItem cartItem = cartItems.stream()
                    .filter(item -> Objects.equals(item.getProduct().getId(), product.getId()))
                    .findFirst()
                    .orElse(null);

            if(cartItem == null){
                if(quantity <= product.getStockQuantity()) {
                    CartItem newCartItem = new CartItem(
                            currentCart, product, quantity
                    );

                    currentCart.getCartItems().add(newCartItem);
                } else {
                    throw new InsufficientStockException("Not enough stock quantity!");
                }

            } else {

                if(cartItem.getQuantity() + quantity <= product.getStockQuantity()) {
                    cartItem.addQuantity(quantity);
                } else {
                    throw new InsufficientStockException("Not enough stock quantity!");
                }
            }

            return cartRepository.save(currentCart);
        } else
            throw new NotActiveProductException("Status of product with ID " + product.getId() + " is not suitable!");
    }

    public Cart removeProductFromCart(Long productId){
        Cart currentCart = getActiveCart();

        CartItem cartItem = currentCart.getCartItems().stream()
                .filter(item -> Objects.equals(item.getProduct().getId(), productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " cannot found in the cart!"));

        currentCart.getCartItems().remove(cartItem);

        return cartRepository.save(currentCart);
    }

    public Cart clearCart(){
        Cart currentCart = getActiveCart();
        currentCart.getCartItems().clear();
        return cartRepository.save(currentCart);
    }
}
