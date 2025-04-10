package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.databinding.ActivityFavoritesBinding;
import com.example.shelfshare.data.BookEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.DocumentSnapshot;

public class FavoritesActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private ActivityFavoritesBinding binding;
    private BookAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<BookEntity> favoriteBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setupRecyclerView();
        loadFavoriteBooks();
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(favoriteBooks, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadFavoriteBooks() {
        String userId = auth.getCurrentUser().getUid();
        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(userId).collection("favorites")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                favoriteBooks.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    BookEntity book = document.toObject(BookEntity.class);
                    if (book != null) {
                        favoriteBooks.add(book);
                    }
                }
                adapter.updateBooks(favoriteBooks);
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(favoriteBooks.isEmpty() ? View.GONE : View.VISIBLE);
                binding.emptyView.setVisibility(favoriteBooks.isEmpty() ? View.VISIBLE : View.GONE);
            })
            .addOnFailureListener(e -> {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error loading favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("cart")
            .document(book.getId())
            .set(book)
            .addOnSuccessListener(aVoid -> 
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> 
                Toast.makeText(this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
} 