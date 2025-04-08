package com.example.shelfshare;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shelfshare.adapters.CartAdapter;
import com.example.shelfshare.databinding.ActivityCartBinding;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.viewmodels.CartViewModel;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemClickListener {
    private ActivityCartBinding binding;
    private CartViewModel cartViewModel;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupCheckoutButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Shopping Cart");
        }
    }

    private void setupRecyclerView() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        adapter = new CartAdapter(this);
        
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);
    }

    private void setupViewModel() {
        cartViewModel.getCartItems().observe(this, items -> {
            adapter.submitList(items);
            binding.emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvCartItems.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        });

        cartViewModel.getTotalPrice().observe(this, total -> {
            binding.tvTotalPrice.setText(String.format("Total: ₹%.2f", total));
        });

        cartViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        cartViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener(v -> {
            if (cartViewModel.getCartItems().getValue() == null || 
                cartViewModel.getCartItems().getValue().isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            // Implement checkout logic here
            Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCartItemClick(BookEntity book) {
        // TODO: Navigate to book details
        Toast.makeText(this, "Book details: " + book.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveClick(BookEntity book) {
        cartViewModel.removeFromCart(book);
        Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuantityChanged(BookEntity book, int newQuantity) {
        cartViewModel.updateQuantity(book, newQuantity);
    }
} 