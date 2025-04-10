package com.example.shelfshare.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.example.shelfshare.viewmodels.CheckoutViewModel.Order;

public class OrderRepository {
    private final FirebaseFirestore db;

    public OrderRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createOrder(Order order) {
        return db.collection("orders")
                .document(order.orderId)
                .set(order);
    }
} 