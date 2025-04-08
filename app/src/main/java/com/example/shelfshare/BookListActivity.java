package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.databinding.ActivityBookListBinding;
import com.example.shelfshare.viewmodels.BookListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private ActivityBookListBinding binding;
    private BookListViewModel viewModel;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Books");
        }

        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);
        books = new ArrayList<>();

        setupRecyclerView();
        setupSearchBar();
        loadBooks();
    }

    private void setupRecyclerView() {
        recyclerView = binding.rvBooks;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new BookAdapter(books, this);
        recyclerView.setAdapter(adapter);
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
        FirebaseFirestore.getInstance()
                .collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    books.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        books.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading books: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterBooks(String query) {
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredBooks.add(book);
            }
        }
        adapter = new BookAdapter(filteredBooks, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBookClick(Book book) {
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra("bookId", book.getId());
        startActivity(intent);
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