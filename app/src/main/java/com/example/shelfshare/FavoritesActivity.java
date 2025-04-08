package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.databinding.ActivityFavoritesBinding;
import com.example.shelfshare.viewmodels.AvailableBooksViewModel;
import com.example.shelfshare.viewmodels.CartViewModel;

public class FavoritesActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener, BookAdapter.OnFavoriteClickListener {
    private ActivityFavoritesBinding binding;
    private AvailableBooksViewModel viewModel;
    private BookAdapter adapter;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Favorites");
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AvailableBooksViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();
        
        // Observe favorite books
        observeViewModel();

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(this);
        adapter.setOnBookClickListener(this);
        adapter.setOnFavoriteClickListener(this);
        
        binding.rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvFavorites.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getFavoriteBooks().observe(this, books -> {
            adapter.updateBooks(books);
            
            // Show/hide empty state
            binding.emptyState.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvFavorites.setVisibility(books.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBookClick(BookEntity book) {
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra("book_id", book.getId());
        startActivity(intent);
    }

    @Override
    public void onBookClick(com.example.shelfshare.models.Book book) {
        // Not used in this activity
    }

    @Override
    public void onFavoriteClick(BookEntity book) {
        viewModel.toggleFavorite(book);
    }

    @Override
    public void onAddToCartClick(BookEntity book) {
        cartViewModel.addToCart(book);
        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 