package com.example.shelfshare;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.databinding.ActivityAvailableBooksBinding;
import com.example.shelfshare.viewmodels.AvailableBooksViewModel;
import java.util.ArrayList;
import java.util.List;

public class AvailableBooksActivity extends AppCompatActivity {

    private ActivityAvailableBooksBinding binding;
    private BookAdapter adapter;
    private AvailableBooksViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvailableBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Available Books");

        setupRecyclerView();
        setupViewModel();
        loadInitialData();
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(new ArrayList<>());
        binding.rvAvailableBooks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAvailableBooks.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AvailableBooksViewModel.class);
        viewModel.getAvailableBooks().observe(this, books -> {
            adapter.updateBooks(books);
        });
    }

    private void loadInitialData() {
        // Sample data - in a real app, this would come from an API or database
        List<BookEntity> sampleBooks = new ArrayList<>();
        sampleBooks.add(new BookEntity("1", "The Great Gatsby", "F. Scott Fitzgerald", "New York", 9.99, true, "https://example.com/gatsby.jpg"));
        sampleBooks.add(new BookEntity("2", "1984", "George Orwell", "Los Angeles", 12.99, true, "https://example.com/1984.jpg"));
        sampleBooks.add(new BookEntity("3", "To Kill a Mockingbird", "Harper Lee", "Chicago", 11.99, true, "https://example.com/mockingbird.jpg"));
        sampleBooks.add(new BookEntity("4", "Pride and Prejudice", "Jane Austen", "Houston", 8.99, true, "https://example.com/pride.jpg"));
        sampleBooks.add(new BookEntity("5", "The Catcher in the Rye", "J.D. Salinger", "Phoenix", 10.99, true, "https://example.com/catcher.jpg"));

        viewModel.insertAll(sampleBooks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_available_books, menu);
        return true;
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