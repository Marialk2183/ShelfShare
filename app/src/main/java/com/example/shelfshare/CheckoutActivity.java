package com.example.shelfshare;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.databinding.ActivityCheckoutBinding;
import com.example.shelfshare.viewmodels.CheckoutViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class CheckoutActivity extends AppCompatActivity {
    private ActivityCheckoutBinding binding;
    private CheckoutViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        setupToolbar();
        setupClickListeners();
        setupObservers();
        updateTotalAmount();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        binding.btnPlaceOrder.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String cardNumber = binding.etCardNumber.getText().toString().trim();
            String expiryDate = binding.etExpiryDate.getText().toString().trim();
            String cvv = binding.etCvv.getText().toString().trim();

            viewModel.placeOrder(fullName, phone, address, cardNumber, expiryDate, cvv);
        });
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnPlaceOrder.setEnabled(!isLoading);
        });

        viewModel.getOrderSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalAmount() {
        double total = viewModel.calculateTotal();
        binding.tvTotalAmount.setText(String.format("Total: ₹%.2f", total));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 