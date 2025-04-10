package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.databinding.ActivityMyBooksBinding;
import com.example.shelfshare.viewmodels.CartViewModel;
import com.example.shelfshare.viewmodels.MyBooksViewModel;
import java.util.ArrayList;
import java.util.List;

public class MyBooksActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private ActivityMyBooksBinding binding;
    private MyBooksViewModel viewModel;
    private BookAdapter adapter;
    private List<BookEntity> books = new ArrayList<>();
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Books");
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MyBooksViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();
        
        // Observe my books
        observeViewModel();

        // Setup return book button
        binding.btnReturnBook.setOnClickListener(view -> {
            // TODO: Implement return book functionality
            Toast.makeText(this, "Return book functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(books, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getMyBooks().observe(this, books -> {
            this.books.clear();
            this.books.addAll(books);
            adapter.notifyDataSetChanged();
            
            // Show/hide empty state
            binding.emptyState.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(books.isEmpty() ? View.GONE : View.VISIBLE);
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
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(BookEntity book) {
        // Implement add to cart functionality
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
