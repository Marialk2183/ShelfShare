package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.databinding.ActivityBookListBinding;
import com.example.shelfshare.viewmodels.BookListViewModel;
import com.example.shelfshare.viewmodels.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private ActivityBookListBinding binding;
    private BookListViewModel viewModel;
    private BookAdapter adapter;
    private CartViewModel cartViewModel;
    private List<BookEntity> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupViewModels();
        setupRecyclerView();
        setupSearchBar();
        loadBooks();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Books");
        }
    }

    private void setupViewModels() {
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(books, this);
        binding.rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvBooks.setAdapter(adapter);
    }

    private void setupSearchBar() {
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });
    }

    private void loadBooks() {
        // Show loading indicator if you have one
        binding.progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance()
            .collection("books")
            .get()
            .addOnCompleteListener(task -> {
                binding.progressBar.setVisibility(View.GONE);
                
                if (task.isSuccessful() && task.getResult() != null) {
                    books.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert Firestore document to BookEntity
                        BookEntity book = new BookEntity();
                        book.setId(document.getId());
                        book.setTitle(document.getString("title"));
                        book.setAuthor(document.getString("author"));
                        book.setImageUrl(document.getString("imageUrl"));
                        book.setPrice(document.getDouble("price") != null ? 
                            document.getDouble("price") : 0.0);
                        books.add(book);
                    }
                    adapter.updateBooks(books);
                } else {
                    String error = task.getException() != null ? 
                        task.getException().getMessage() : "Unknown error";
                    Toast.makeText(this, "Error loading books: " + error, 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void filterBooks(String query) {
        if (query == null || query.isEmpty()) {
            // If query is empty, restore original list
            loadBooks();
            return;
        }

        List<BookEntity> allBooks = adapter.getBooks();
        List<BookEntity> filteredList = new ArrayList<>();
        
        String lowercaseQuery = query.toLowerCase().trim();
        for (BookEntity book : allBooks) {
            if (book.getTitle().toLowerCase().contains(lowercaseQuery) ||
                book.getAuthor().toLowerCase().contains(lowercaseQuery)) {
                filteredList.add(book);
            }
        }
        adapter.updateBooks(filteredList);
    }

    public List<BookEntity> getBooks() {
        return adapter.getBooks();
    }

    @Override
    public void onBookClick(BookEntity book) {
        Intent intent = new Intent(this, BookListActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(BookEntity book) {
        // Implement add to cart functionality
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    () -> finish()
                );
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}