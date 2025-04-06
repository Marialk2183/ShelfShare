package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.models.Book;
import com.example.shelfshare.models.CartItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public CartViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<CartItem>> getCartItems() {
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

    public void addToCart(Book book) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null) {
            currentItems = new ArrayList<>();
        }

        boolean found = false;
        for (CartItem item : currentItems) {
            if (item.getBook().getId().equals(book.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            currentItems.add(new CartItem(book, 1));
        }

        cartItems.setValue(currentItems);
        calculateTotal();
    }

    public void removeFromCart(String bookId) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null) return;

        currentItems.removeIf(item -> item.getBook().getId().equals(bookId));
        cartItems.setValue(currentItems);
        calculateTotal();
    }

    public void updateQuantity(String bookId, int quantity) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null) return;

        for (CartItem item : currentItems) {
            if (item.getBook().getId().equals(bookId)) {
                item.setQuantity(quantity);
                break;
            }
        }

        cartItems.setValue(currentItems);
        calculateTotal();
    }

    private void calculateTotal() {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null) return;

        double total = 0;
        for (CartItem item : currentItems) {
            total += item.getBook().getPrice() * item.getQuantity();
        }
        totalAmount.setValue(total);
    }

    public void checkout(String userId, String location) {
        isLoading.setValue(true);
        List<CartItem> currentItems = cartItems.getValue();
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
        for (CartItem item : currentItems) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("title", item.getBook().getTitle());
            itemData.put("price", item.getBook().getPrice());
            items.put(item.getBook().getId(), itemData);
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