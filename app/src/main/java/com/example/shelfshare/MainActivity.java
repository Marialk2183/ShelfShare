package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import com.example.shelfshare.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.viewmodels.CartViewModel;
import com.example.shelfshare.adapters.BookAdapter;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupToolbar();
        setupDrawerNavigation();
        setupBottomNavigation();
        
        // Update navigation header with user info
        updateNavigationHeader(currentUser);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_rentals) {
                intent = new Intent(this, RentalActivity.class);
            } else if (itemId == R.id.nav_location) {
                intent = new Intent(this, LocationSelectionActivity.class);
            } else if (itemId == R.id.nav_favorites) {
                intent = new Intent(this, FavoritesActivity.class);
            } else if (itemId == R.id.nav_cart) {
                intent = new Intent(this, CartActivity.class);
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(this, UserProfileActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Set initial selection
        binding.bottomNavigation.setSelectedItemId(R.id.nav_rentals);
    }

    private void updateNavigationHeader(FirebaseUser user) {
        View headerView = binding.navView.getHeaderView(0);
        if (headerView != null) {
            TextView userName = headerView.findViewById(R.id.tv_user_name);
            TextView userEmail = headerView.findViewById(R.id.tv_user_email);
            
            if (user.getDisplayName() != null) {
                userName.setText(user.getDisplayName());
            }
            if (user.getEmail() != null) {
                userEmail.setText(user.getEmail());
            }
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("ShelfShare");
        }
    }

    private void setupDrawerNavigation() {
        drawerLayout = binding.drawerLayout;
        binding.navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_my_books) {
                intent = new Intent(this, MyBooksActivity.class);
            } else if (itemId == R.id.nav_add_book) {
                intent = new Intent(this, AddBookActivity.class);
            } else if (itemId == R.id.nav_available_books) {
                intent = new Intent(this, AvailableBooksActivity.class);
            } else if (itemId == R.id.nav_settings) {
                intent = new Intent(this, SettingsActivity.class);
            } else if (itemId == R.id.nav_logout) {
                // Handle logout
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            if (intent != null) {
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBookClick(BookEntity book) {
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra("book_id", book.getId());
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(BookEntity book) {
        cartViewModel.addToCart(book);
        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
    }
} 