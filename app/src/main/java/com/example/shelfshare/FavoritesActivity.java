package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.viewmodels.FavoritesViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class FavoritesActivity extends AppCompatActivity {
    private FavoritesViewModel viewModel;
    private RecyclerView rvFavorites;
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        setupToolbar();
        setupViews();
        setupObservers();
        viewModel.loadFavorites();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Favorites");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookAdapter(book -> {
            // Handle book click
            Intent intent = new Intent(this, BookDetailsActivity.class);
            intent.putExtra("bookId", book.getId());
            startActivity(intent);
        });
        rvFavorites.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getFavorites().observe(this, books -> {
            adapter.submitList(books);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 