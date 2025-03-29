package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivityPaymentBinding;
import com.example.shelfshare.utils.CartManager;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = CartManager.getInstance();
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
                processCardPayment();
            } else if (binding.rbPaytm.isChecked()) {
                processPaytmPayment();
            } else if (binding.rbCOD.isChecked()) {
                processCODPayment();
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processCardPayment() {
        // Simulate card payment processing
        binding.btnProceedToPay.setEnabled(false);
        binding.btnProceedToPay.setText("Processing...");
        
        new Handler().postDelayed(() -> {
            Toast.makeText(this, "Card payment processed successfully!", Toast.LENGTH_SHORT).show();
            completePayment();
        }, 2000);
    }

    private void processPaytmPayment() {
        // Simulate Paytm payment processing
        binding.btnProceedToPay.setEnabled(false);
        binding.btnProceedToPay.setText("Processing...");
        
        new Handler().postDelayed(() -> {
            Toast.makeText(this, "Paytm payment processed successfully!", Toast.LENGTH_SHORT).show();
            completePayment();
        }, 2000);
    }

    private void processCODPayment() {
        Toast.makeText(this, "Order placed successfully! Payment will be collected on delivery.", Toast.LENGTH_LONG).show();
        completePayment();
    }

    private void completePayment() {
        cartManager.clearCart();
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
} 