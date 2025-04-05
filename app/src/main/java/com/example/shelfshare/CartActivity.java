package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.adapters.CartAdapter;
import com.example.shelfshare.models.CartItem;
import com.example.shelfshare.viewmodels.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class CartActivity extends AppCompatActivity {
    private CartViewModel viewModel;
    private RecyclerView rvCart;
    private CircularProgressIndicator progressBar;
    private TextView tvEmpty, tvTotal;
    private MaterialButton btnCheckout;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        setupObservers();

        // Load cart items
        viewModel.loadCartItems();
    }

    private void initializeViews() {
        rvCart = findViewById(R.id.rvCart);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Shopping Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(
            cartItem -> viewModel.updateQuantity(cartItem, cartItem.getQuantity() + 1),
            cartItem -> viewModel.updateQuantity(cartItem, cartItem.getQuantity() - 1),
            cartItem -> viewModel.removeFromCart(cartItem)
        );
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        btnCheckout.setOnClickListener(v -> {
            if (viewModel.getCartItems().getValue() != null && 
                !viewModel.getCartItems().getValue().isEmpty()) {
                // Navigate to checkout
                Intent intent = new Intent(this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupObservers() {
        viewModel.getCartItems().observe(this, cartItems -> {
            cartAdapter.submitList(cartItems);
            tvEmpty.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
            btnCheckout.setEnabled(!cartItems.isEmpty());
        });

        viewModel.getTotal().observe(this, total -> {
            tvTotal.setText(String.format("Total: ₹%.2f", total));
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            rvCart.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });
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