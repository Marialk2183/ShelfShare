package com.example.shelfshare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivityAddBookBinding;

public class AddBookActivity extends AppCompatActivity {

    private ActivityAddBookBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAddBook.setOnClickListener(view -> {
            // Implement logic to add a book (e.g., save to database)
            finish();
        });
    }
}
