package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("ShelfShare");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Set up click listeners for all cards
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Available Books
        binding.cardAvailableBooks.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AvailableBooksActivity.class);
            startActivity(intent);
        });

        // My Books
        binding.cardMyBooks.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MyBooksActivity.class);
            startActivity(intent);
        });

        // Add Book
        binding.cardAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddBookActivity.class);
            startActivity(intent);
        });

        // Favorites
        binding.cardFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        // Profile
        binding.cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Cart
        binding.cardCart.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        // Show confirmation dialog before exiting
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", (dialog, which) -> {
                finishAffinity();
            })
            .setNegativeButton("No", null)
            .show();
    }
}
