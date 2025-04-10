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
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.databinding.ActivityDashboardBinding;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.models.Location;
import com.example.shelfshare.viewmodels.DashboardViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private ActivityDashboardBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DashboardViewModel viewModel;
    TextView tvCurrentLocation;
    private BookAdapter adapter ;

    private String selectedCategory = "All";
    private Location selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);

        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        MaterialToolbar toolbar = (MaterialToolbar) binding.toolbar;

        setupToolbar(toolbar);
        setupDrawerNavigation();
        setupUserInfo();
        setupRecyclerView();
        setupViewModel();
        setupCategoryChips();
    }

    private void setupToolbar(MaterialToolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("Dashboard");
        }
    }



    private void setupDrawerNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
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

    private void setupUserInfo() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_user_email);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getDisplayName() != null) {
                tvUserName.setText(currentUser.getDisplayName());
            }
            if (currentUser.getEmail() != null) {
                tvUserEmail.setText(currentUser.getEmail());
            }
        }
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.getBooks().observe(this, books -> {
            if (books != null && !books.isEmpty()) {
                adapter.setBooks(books);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "No books available", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.recyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.getSelectedLocation().observe(this, location -> {
            selectedLocation = location;
            if (location != null) {
                tvCurrentLocation.setText("Current Location: " + location.getName());
                viewModel.loadBooksByLocationAndCategory(location.getId(), selectedCategory);
            } else {
                tvCurrentLocation.setText("Current Location: None selected");
                viewModel.loadBooksByCategory(selectedCategory);
            }
        });
    }

    private void setupCategoryChips() {
        binding.chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId); // ✅ Corrected line

            if (chip != null) {
                selectedCategory = chip.getText().toString();

                if (selectedLocation != null) {
                    viewModel.loadBooksByLocationAndCategory(selectedLocation.getId(), selectedCategory);
                } else {
                    viewModel.loadBooksByCategory(selectedCategory);
                }
            }
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
    public void onBookClick(Book book) {

    }

    @Override
    public void onBookClick(BookEntity book) {
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra("book_id", book.getId());
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(BookEntity book) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
