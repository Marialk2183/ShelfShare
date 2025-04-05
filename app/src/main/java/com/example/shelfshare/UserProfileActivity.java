package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivityUserProfileBinding;
import com.example.shelfshare.utils.CartManager;
import com.example.shelfshare.utils.FavoritesManager;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private String currentLocation = "Not Set";
    private CartManager cartManager;
    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = CartManager.getInstance();
        favoritesManager = FavoritesManager.getInstance();

        setupClickListeners();
        updateUI();
    }

    private void setupClickListeners() {
        // Rent button
        binding.btnRent.setOnClickListener(view -> {
            // Navigate to available books for rent
            Intent intent = new Intent(this, AvailableBooksActivity.class);
            startActivity(intent);
        });

        // Cart button
        binding.btnCart.setOnClickListener(view -> {
            if (cartManager.isCartEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
        });

        // Favorites button
        binding.btnFavorites.setOnClickListener(view -> {
            if (favoritesManager.getFavoriteBooks().isEmpty()) {
                Toast.makeText(this, "No favorite books yet", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        });

        // Change Location button
        binding.btnChangeLocation.setOnClickListener(view -> {
            showLocationDialog();
        });
    }

    private void showLocationDialog() {
        String[] locations = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix"};
        new AlertDialog.Builder(this)
            .setTitle("Select Location")
            .setItems(locations, (dialog, which) -> {
                updateLocation(locations[which]);
            })
            .show();
    }

    private void updateLocation(String newLocation) {
        currentLocation = newLocation;
        binding.tvCurrentLocation.setText("Current Location: " + currentLocation);
        Toast.makeText(this, "Location updated to " + newLocation, Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        // Update cart count if needed
        int cartCount = cartManager.getCartItems().size();
        if (cartCount > 0) {
            binding.btnCart.setText("Cart (" + cartCount + ")");
        }

        // Update favorites count if needed
        int favoritesCount = favoritesManager.getFavoriteBooks().size();
        if (favoritesCount > 0) {
            binding.btnFavorites.setText("Favorites (" + favoritesCount + ")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
} 