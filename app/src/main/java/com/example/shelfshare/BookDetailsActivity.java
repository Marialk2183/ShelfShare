package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.dialogs.RentalConfirmationDialog;
import com.example.shelfshare.viewmodels.BookDetailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class BookDetailsActivity extends AppCompatActivity {
    private BookDetailsViewModel viewModel;
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvDescription;
    private TextView tvPrice;
    private TextView tvCategory;
    private FloatingActionButton fabFavorite;
    private Button btnRent;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivCover = findViewById(R.id.ivCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvCategory = findViewById(R.id.tvCategory);
        fabFavorite = findViewById(R.id.fabFavorite);
        btnRent = findViewById(R.id.btnRent);
        progressBar = findViewById(R.id.progressBar);

        String bookId = getIntent().getStringExtra("book_id");
        if (bookId == null) {
            Toast.makeText(this, "Book ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(BookDetailsViewModel.class);
        observeViewModel();
        viewModel.loadBookDetails(bookId);

        fabFavorite.setOnClickListener(v -> {
            if (viewModel.getIsFavorite().getValue() != null && viewModel.getIsFavorite().getValue()) {
                viewModel.removeFromFavorites(bookId);
            } else {
                viewModel.addToFavorites(bookId);
            }
        });

        btnRent.setOnClickListener(v -> {
            BookEntity book = viewModel.getBook().getValue();
            if (book != null) {
                RentalConfirmationDialog dialog = RentalConfirmationDialog.newInstance(book, 7, book.getPrice());
                dialog.setOnRentalConfirmedListener((bookEntity, days, price) -> {
                    viewModel.rentBook(bookEntity, days, price);
                });
                dialog.show(getSupportFragmentManager(), "rental_confirmation");
            }
        });
    }

    private void observeViewModel() {
        viewModel.getBook().observe(this, book -> {
            if (book != null) {
                tvTitle.setText(book.getTitle());
                tvAuthor.setText(book.getAuthor());
                tvDescription.setText(book.getDescription());
                tvPrice.setText(String.format("$%.2f", book.getPrice()));
                tvCategory.setText(book.getCategory());

                if (book.getImageUrl() != null) {
                    Glide.with(this)
                            .load(book.getImageUrl())
                            .into(ivCover);
                }
            }
        });

        viewModel.getIsFavorite().observe(this, isFavorite -> {
            fabFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnRent.setEnabled(!isLoading);
            fabFavorite.setEnabled(!isLoading);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getRentSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Book rented successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
