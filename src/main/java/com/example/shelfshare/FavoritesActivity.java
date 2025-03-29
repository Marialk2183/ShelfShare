package com.example.shelfshare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.databinding.ActivityFavoritesBinding;
import com.example.shelfshare.utils.FavoritesManager;

public class FavoritesActivity extends AppCompatActivity {

    private ActivityFavoritesBinding binding;
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Favorite Books");

        setupRecyclerView();
        loadFavorites();
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(FavoritesManager.getInstance().getFavoriteBooks());
        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {
        adapter.updateBooks(FavoritesManager.getInstance().getFavoriteBooks());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 