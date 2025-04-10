package com.example.shelfshare.utils;

import com.example.shelfshare.data.BookEntity;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<BookEntity> cartItems;
    private final List<Integer> quantities;
    private OnCartChangeListener listener;

    private CartManager() {
        cartItems = new ArrayList<>();
        quantities = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(BookEntity book) {
        int index = cartItems.indexOf(book);
        if (index != -1) {
            quantities.set(index, quantities.get(index) + 1);
        } else {
            cartItems.add(book);
            quantities.add(1);
        }
        notifyCartChanged();
    }

    public void removeFromCart(BookEntity book) {
        int index = cartItems.indexOf(book);
        if (index != -1) {
            cartItems.remove(index);
            quantities.remove(index);
        }
        notifyCartChanged();
    }

    public void updateQuantity(BookEntity book, int quantity) {
        if (cartItems.contains(book)) {
            int index = cartItems.indexOf(book);
            quantities.set(index, quantity);
            notifyCartChanged();
        }
    }

    public List<BookEntity> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getQuantity(BookEntity book) {
        int index = cartItems.indexOf(book);
        return index != -1 ? quantities.get(index) : 0;
    }

    public void increaseQuantity(BookEntity book) {
        int index = cartItems.indexOf(book);
        if (index != -1) {
            quantities.set(index, quantities.get(index) + 1);
        }
        notifyCartChanged();
    }

    public void decreaseQuantity(BookEntity book) {
        int index = cartItems.indexOf(book);
        if (index != -1) {
            int currentQuantity = quantities.get(index);
            if (currentQuantity > 1) {
                quantities.set(index, currentQuantity - 1);
            } else {
                removeFromCart(book);
            }
        }
        notifyCartChanged();
    }

    public double calculateTotal() {
        double total = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            total += cartItems.get(i).getPrice() * quantities.get(i);
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
        quantities.clear();
        notifyCartChanged();
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public double getTotalAmount() {
        return calculateTotal();
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.listener = listener;
    }

    private void notifyCartChanged() {
        if (listener != null) {
            listener.onCartChanged();
        }
    }

    public interface OnCartChangeListener {
        void onCartChanged();
    }
} 