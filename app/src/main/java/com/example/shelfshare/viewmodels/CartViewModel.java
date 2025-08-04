package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.CartItem;
import com.example.shelfshare.utils.CartManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<BookEntity>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>("");
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public CartViewModel() {
        loadCartItems();
    }

    public LiveData<List<BookEntity>> getCartItems() {
        return cartItems;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public void loadCartItems() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            error.setValue("User not logged in");
            return;
        }

        isLoading.setValue(true);
        error.setValue("");

        db.collection("carts")
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<BookEntity> items = new ArrayList<>();
                for (var doc : queryDocumentSnapshots) {
                    BookEntity book = doc.toObject(BookEntity.class);
                    book.setId(doc.getId());
                    items.add(book);
                }
                cartItems.setValue(items);
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                error.setValue("Failed to load cart: " + e.getMessage());
                isLoading.setValue(false);
            });
    }

    public void addToCart(BookEntity book) {
        List<BookEntity> currentItems = cartItems.getValue();
        if (currentItems == null) currentItems = new ArrayList<>();

        // Check if book already exists in cart
        boolean found = false;
        for (BookEntity item : currentItems) {
            if (item.getId().equals(book.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            book.setQuantity(1);
            currentItems.add(book);
        }

        cartItems.setValue(currentItems);
        calculateTotal();
    }

    public void removeFromCart(BookEntity book) {
        List<BookEntity> currentItems = cartItems.getValue();
        if (currentItems != null) {
            currentItems.removeIf(item -> item.getId().equals(book.getId()));
            cartItems.setValue(currentItems);
            calculateTotal();
        }
    }

    public void updateQuantity(BookEntity book, int quantity) {
        // Implementation needed
    }

    private void calculateTotal() {
        List<BookEntity> items = cartItems.getValue();
        if (items != null) {
            double total = 0;
            for (BookEntity item : items) {
                total += item.getPrice() * item.getQuantity();
            }
            totalPrice.setValue(total);
        }
    }

    public void clearCart() {
        // Implementation needed
    }

    public void checkout(String userId, String location) {
        // Implementation needed
    }
} 