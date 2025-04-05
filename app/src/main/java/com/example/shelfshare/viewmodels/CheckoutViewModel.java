package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.utils.CartManager;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class CheckoutViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> checkoutSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final CartManager cartManager;
    private final FirebaseFirestore db;

    public CheckoutViewModel() {
        cartManager = CartManager.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getCheckoutSuccess() {
        return checkoutSuccess;
    }

    public LiveData<String> getError() {
        return error;
    }

    public List<Book> getCartItems() {
        return cartManager.getCartItems();
    }

    public double calculateTotal() {
        return cartManager.calculateTotal();
    }

    public void placeOrder(String fullName, String phone, String address, 
                         String cardNumber, String expiryDate, String cvv) {
        if (!validateInputs(fullName, phone, address, cardNumber, expiryDate, cvv)) {
            return;
        }

        isLoading.setValue(true);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String orderId = UUID.randomUUID().toString();

        // Create order document
        db.collection("orders")
                .document(orderId)
                .set(createOrderData(userId, fullName, phone, address, orderId))
                .addOnSuccessListener(aVoid -> {
                    // Clear cart and update success state
                    cartManager.clearCart();
                    checkoutSuccess.setValue(true);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to place order: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    private boolean validateInputs(String fullName, String phone, String address,
                                 String cardNumber, String expiryDate, String cvv) {
        if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty() ||
            cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            error.setValue("Please fill in all fields");
            return false;
        }

        if (cardNumber.length() != 16) {
            error.setValue("Invalid card number");
            return false;
        }

        if (cvv.length() != 3) {
            error.setValue("Invalid CVV");
            return false;
        }

        return true;
    }

    private Object createOrderData(String userId, String fullName, String phone,
                                 String address, String orderId) {
        return new Object() {
            public String orderId = orderId;
            public String userId = userId;
            public String fullName = fullName;
            public String phone = phone;
            public String address = address;
            public List<Book> items = getCartItems();
            public double total = calculateTotal();
            public long timestamp = System.currentTimeMillis();
            public String status = "pending";
        };
    }

    public void processCheckout(String userId, String location, Map<String, Book> cartItems) {
        isLoading.setValue(true);

        // Create order data
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("location", location);
        orderData.put("timestamp", System.currentTimeMillis());
        orderData.put("status", "pending");
        orderData.put("items", cartItems);

        // Calculate total amount
        double totalAmount = 0;
        for (Book book : cartItems.values()) {
            totalAmount += book.getPrice();
        }
        orderData.put("totalAmount", totalAmount);

        // Create order in Firestore
        db.collection("orders")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    // Update book availability for each item
                    for (Book book : cartItems.values()) {
                        db.collection("books")
                                .document(book.getId())
                                .update("available", false);
                    }
                    checkoutSuccess.setValue(true);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to process checkout");
                    isLoading.setValue(false);
                });
    }
} 