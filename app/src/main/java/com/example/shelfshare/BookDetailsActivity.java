package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.viewmodels.BookDetailsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;

public class BookDetailsActivity extends AppCompatActivity {
    private BookDetailsViewModel viewModel;
    private CircularProgressIndicator progressBar;
    private TextView tvBookTitle, tvAuthor, tvPrice, tvLocation, tvDescription;
    private RatingBar ratingBar;
    private MaterialButton btnRent, btnAddToFavorites;
    private String bookId;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Get book ID from intent
        bookId = getIntent().getStringExtra("book_id");
        if (bookId == null) {
            finish();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(BookDetailsViewModel.class);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupClickListeners();
        setupObservers();

        // Load book details
        viewModel.loadBookDetails(bookId);
        viewModel.checkIfFavorite(bookId);
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        tvBookTitle = findViewById(R.id.tvBookTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPrice = findViewById(R.id.tvPrice);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);
        ratingBar = findViewById(R.id.ratingBar);
        btnRent = findViewById(R.id.btnRent);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        btnRent.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            viewModel.rentBook(bookId);
        });

        btnAddToFavorites.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            if (isFavorite) {
                viewModel.removeFromFavorites(bookId);
            } else {
                viewModel.addToFavorites(bookId);
            }
        });
    }

    private void setupObservers() {
        viewModel.getBook().observe(this, book -> {
            if (book != null) {
                updateUI(book);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsFavorite().observe(this, favorite -> {
            isFavorite = favorite;
            updateFavoriteButton();
        });

        viewModel.getRentSuccess().observe(this, success -> {
            if (success) {
                // Show success message
                // Update UI accordingly
            }
        });
    }

    private void updateUI(Book book) {
        tvBookTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvPrice.setText(String.format("₹%.2f/day", book.getPrice()));
        tvLocation.setText(book.getLocation());
        tvDescription.setText(book.getDescription());
        ratingBar.setRating(book.getRating());

        // Load book cover image
        Glide.with(this)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_book_placeholder)
                .into(findViewById(R.id.ivBookCover));
    }

    private void updateFavoriteButton() {
        btnAddToFavorites.setIconResource(isFavorite ? 
            R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        btnAddToFavorites.setText(isFavorite ? 
            "Remove from Favorites" : "Add to Favorites");
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
