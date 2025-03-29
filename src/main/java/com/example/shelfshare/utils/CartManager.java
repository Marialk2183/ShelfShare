package com.example.shelfshare.utils;

import com.example.shelfshare.data.BookEntity;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<BookEntity> cartItems;
    private double totalAmount;

    private CartManager() {
        cartItems = new ArrayList<>();
        totalAmount = 0.0;
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(BookEntity book) {
        if (!cartItems.contains(book)) {
            cartItems.add(book);
            totalAmount += book.getPrice();
        }
    }

    public void removeFromCart(BookEntity book) {
        if (cartItems.remove(book)) {
            totalAmount -= book.getPrice();
        }
    }

    public void clearCart() {
        cartItems.clear();
        totalAmount = 0.0;
    }

    public List<BookEntity> getCartItems() {
        return cartItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
} 