package com.example.shelfshare.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.repositories.OrderRepository;
import com.example.shelfshare.utils.CartManager;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class CheckoutViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> checkoutSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final CartManager cartManager;
    private final FirebaseFirestore db;
    private final OrderRepository orderRepository;
    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>();

    public CheckoutViewModel(Application application) {
        super(application);
        cartManager = CartManager.getInstance();
        db = FirebaseFirestore.getInstance();
        orderRepository = new OrderRepository();
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
        List<BookEntity> bookEntities = cartManager.getCartItems();
        List<Book> books = new ArrayList<>();
        for (BookEntity entity : bookEntities) {
            books.add(convertToBook(entity));
        }
        return books;
    }

    public double calculateTotal() {
        return cartManager.calculateTotal();
    }

    public void placeOrder(String fullName, String phone, String address) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<BookEntity> cartItems = cartManager.getCartItems();
        
        if (cartItems.isEmpty()) {
            error.setValue("Cart is empty");
            return;
        }

        String orderId = UUID.randomUUID().toString();
        Order order = new Order(
            orderId,
            userId,
            fullName,
            phone,
            address,
            cartItems,
            calculateTotal(),
            System.currentTimeMillis()
        );

        orderRepository.createOrder(order)
            .addOnSuccessListener(aVoid -> {
                cartManager.clearCart();
                checkoutSuccess.setValue(true);
            })
            .addOnFailureListener(e -> error.setValue(e.getMessage()));
    }

    private Book convertToBook(BookEntity entity) {
        Book book = new Book();
        book.setId(entity.getId());
        book.setTitle(entity.getTitle());
        book.setAuthor(entity.getAuthor());
        book.setLocation(entity.getLocation());
        book.setPrice(entity.getPrice());
        book.setAvailable(entity.isAvailable());
        book.setImageUrl(entity.getImageUrl());
        return book;
    }

    public static class Order {
        public String orderId;
        public String userId;
        public String fullName;
        public String phone;
        public String address;
        public List<BookEntity> items;
        public double totalAmount;
        public long createdAt;

        public Order(String orderId, String userId, String fullName, String phone, String address, List<BookEntity> items, double totalAmount, long createdAt) {
            this.orderId = orderId;
            this.userId = userId;
            this.fullName = fullName;
            this.phone = phone;
            this.address = address;
            this.items = items;
            this.totalAmount = totalAmount;
            this.createdAt = createdAt;
        }
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

    public LiveData<Boolean> getOrderSuccess() {
        return orderSuccess;
    }
} 