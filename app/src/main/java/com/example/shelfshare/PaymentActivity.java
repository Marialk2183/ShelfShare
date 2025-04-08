package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.databinding.ActivityPaymentBinding;
import com.example.shelfshare.utils.CartManager;
import com.example.shelfshare.utils.RazorpayHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity implements RazorpayHelper.PaymentCallback {

    private ActivityPaymentBinding binding;
    private CartManager cartManager;
    private FirebaseFirestore db;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = CartManager.getInstance();
        db = FirebaseFirestore.getInstance();
        orderId = UUID.randomUUID().toString();
        
        updateTotalAmount();
        setupClickListeners();
    }

    private void updateTotalAmount() {
        double total = cartManager.getTotalAmount();
        binding.tvTotalAmount.setText(String.format("Total Amount: $%.2f", total));
    }

    private void setupClickListeners() {
        binding.btnProceedToPay.setOnClickListener(view -> {
            if (cartManager.isCartEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.rbCard.isChecked()) {
                processRazorpayPayment();
            } else if (binding.rbPaytm.isChecked()) {
                processPaytmPayment();
            } else if (binding.rbCOD.isChecked()) {
                processCODPayment();
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processRazorpayPayment() {
        double amount = cartManager.getTotalAmount();
        RazorpayHelper razorpayHelper = new RazorpayHelper(this, amount, orderId, this);
        razorpayHelper.startPayment();
    }

    private void processPaytmPayment() {
        // Implement Paytm payment
        Toast.makeText(this, "Paytm payment not implemented yet", Toast.LENGTH_SHORT).show();
    }

    private void processCODPayment() {
        completeOrder("COD");
    }

    @Override
    public void onPaymentSuccess(String paymentId) {
        completeOrder(paymentId);
    }

    @Override
    public void onPaymentError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        binding.btnProceedToPay.setEnabled(true);
        binding.btnProceedToPay.setText("Proceed to Pay");
    }

    private void completeOrder(String paymentId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        // Create order in Firestore
        db.collection("orders")
            .document(orderId)
            .set(new Order(
                orderId,
                userId,
                cartManager.getCartItems(),
                cartManager.getTotalAmount(),
                paymentId,
                System.currentTimeMillis()
            ))
            .addOnSuccessListener(aVoid -> {
                cartManager.clearCart();
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error placing order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private static class Order {
        String orderId;
        String userId;
        List<BookEntity> items;
        double totalAmount;
        String paymentId;
        long timestamp;

        Order(String orderId, String userId, List<BookEntity> items, double totalAmount, String paymentId, long timestamp) {
            this.orderId = orderId;
            this.userId = userId;
            this.items = items;
            this.totalAmount = totalAmount;
            this.paymentId = paymentId;
            this.timestamp = timestamp;
        }
    }
} 