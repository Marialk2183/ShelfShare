package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.viewmodels.BookListViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;

public class BookListActivity extends AppCompatActivity {
    private BookListViewModel viewModel;
    private RecyclerView rvBooks;
    private CircularProgressIndicator progressBar;
    private TextView tvEmpty;
    private SearchBar searchBar;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        // Get category ID and name from intent
        String categoryId = getIntent().getStringExtra("category_id");
        String categoryName = getIntent().getStringExtra("category_name");

        // Initialize views
        initializeViews();
        setupToolbar(categoryName);
        setupRecyclerView();
        setupSearchBar();
        setupObservers();

        // Load books
        if (categoryId != null) {
            viewModel.loadBooksByCategory(categoryId);
        } else {
            viewModel.loadAllBooks();
        }
    }

    private void initializeViews() {
        rvBooks = findViewById(R.id.rvBooks);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        searchBar = findViewById(R.id.searchBar);
    }

    private void setupToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(book -> {
            // Handle book click
            Intent intent = new Intent(BookListActivity.this, BookDetailsActivity.class);
            intent.putExtra("book_id", book.getId());
            startActivity(intent);
        });
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        rvBooks.setAdapter(bookAdapter);
    }

    private void setupSearchBar() {
        searchBar.setOnQueryTextListener(new SearchBar.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.searchBooks(newText);
                return true;
            }
        });
    }

    private void setupObservers() {
        viewModel.getBooks().observe(this, books -> {
            bookAdapter.submitList(books);
            tvEmpty.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            rvBooks.setVisibility(isLoading ? View.GONE : View.VISIBLE);
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