package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.CartItem;
import com.example.shelfshare.utils.CartManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {
    private final CartManager cartManager;
    private final MutableLiveData<List<BookEntity>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Double> totalAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public CartViewModel() {
        cartManager = CartManager.getInstance();
        db = FirebaseFirestore.getInstance();
        loadCartItems();
    }

    public LiveData<List<BookEntity>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadCartItems() {
        cartItems.setValue(cartManager.getCartItems());
    }

    public void addToCart(BookEntity book) {
        cartManager.addToCart(book);
        loadCartItems();
        calculateTotal();
    }

    public void removeFromCart(BookEntity book) {
        cartManager.removeFromCart(book);
        loadCartItems();
        calculateTotal();
    }

    public void updateQuantity(BookEntity book, int quantity) {
        cartManager.updateQuantity(book, quantity);
        loadCartItems();
        calculateTotal();
    }

    public double calculateTotal() {
        return cartManager.calculateTotal();
    }

    public void clearCart() {
        cartManager.clearCart();
        loadCartItems();
    }

    public void checkout(String userId, String location) {
        isLoading.setValue(true);
        List<BookEntity> currentItems = cartItems.getValue();
        if (currentItems == null || currentItems.isEmpty()) {
            error.setValue("Cart is empty");
            isLoading.setValue(false);
            return;
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("location", location);
        orderData.put("timestamp", System.currentTimeMillis());
        orderData.put("status", "pending");
        orderData.put("totalAmount", totalAmount.getValue());

        Map<String, Object> items = new HashMap<>();
        for (BookEntity item : currentItems) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("title", item.getTitle());
            itemData.put("price", item.getPrice());
            items.put(item.getId(), itemData);
        }
        orderData.put("items", items);

        db.collection("orders")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    // Clear cart
                    cartItems.setValue(new ArrayList<>());
                    totalAmount.setValue(0.0);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to process checkout");
                    isLoading.setValue(false);
                });
    }
} 