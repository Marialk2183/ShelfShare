package com.example.shelfshare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivityBookDetailsBinding;

public class BookDetailsActivity extends AppCompatActivity {

    private ActivityBookDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example: Set book details (typically passed via intent extras)
        binding.tvBookTitle.setText("Sample Book Title");
        binding.tvBookAuthor.setText("Sample Author");
        binding.tvBookLocation.setText("Sample Location");
        binding.tvBookPrice.setText("$9.99"); // Example price

        binding.btnRentOrReturn.setOnClickListener(view -> {
            // Implement renting/returning logic here
        });
    }
}
