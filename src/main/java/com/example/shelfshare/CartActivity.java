package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shelfshare.databinding.ActivityCartBinding;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.utils.CartManager;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private List<BookEntity> cartItems;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Shopping Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize cart manager and items list
        cartManager = CartManager.getInstance();
        cartItems = cartManager.getCartItems();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up checkout button
        binding.btnCheckout.setOnClickListener(v -> proceedToCheckout());

        // Update UI
        updateUI();
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItems, this::updateUI);
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(cartAdapter);
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            binding.tvEmptyCart.setVisibility(View.VISIBLE);
            binding.llCheckout.setVisibility(View.GONE);
        } else {
            binding.tvEmptyCart.setVisibility(View.GONE);
            binding.llCheckout.setVisibility(View.VISIBLE);
            updateTotalAmount();
        }
    }

    private void updateTotalAmount() {
        double totalAmount = cartManager.getTotalAmount();
        binding.tvTotalAmount.setText(String.format("$%.2f", totalAmount));
    }

    private void proceedToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to payment screen
        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 