package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.adapters.CategoryAdapter;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.models.Category;
import com.example.shelfshare.viewmodels.DashboardViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DashboardViewModel viewModel;
    private RecyclerView rvCategories, rvBooks;
    private TextView tvLocation;
    private CategoryAdapter categoryAdapter;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupRecyclerViews();
        setupObservers();
        setupClickListeners();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        rvCategories = findViewById(R.id.rvCategories);
        rvBooks = findViewById(R.id.rvBooks);
        tvLocation = findViewById(R.id.tvLocation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        updateNavigationHeader();
    }

    private void updateNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView ivProfile = headerView.findViewById(R.id.ivProfile);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);
        TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);

        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                tvUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
                tvUserEmail.setText(user.getEmail());
                // TODO: Load profile image
            }
        });
    }

    private void setupRecyclerViews() {
        // Setup Categories RecyclerView
        categoryAdapter = new CategoryAdapter(category -> {
            // Handle category click
            Intent intent = new Intent(DashboardActivity.this, BookListActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("category_name", category.getName());
            startActivity(intent);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        // Setup Books RecyclerView
        bookAdapter = new BookAdapter(book -> {
            // Handle book click
            Intent intent = new Intent(DashboardActivity.this, BookDetailsActivity.class);
            intent.putExtra("book_id", book.getId());
            startActivity(intent);
        });
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        rvBooks.setAdapter(bookAdapter);
    }

    private void setupObservers() {
        // Observe categories
        viewModel.getCategories().observe(this, categories -> {
            categoryAdapter.submitList(categories);
        });

        // Observe books
        viewModel.getBooks().observe(this, books -> {
            bookAdapter.submitList(books);
        });

        // Observe location
        viewModel.getCurrentLocation().observe(this, location -> {
            if (location != null && !location.isEmpty()) {
                tvLocation.setText(location);
            }
        });
    }

    private void setupClickListeners() {
        findViewById(R.id.btnChangeLocation).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LocationSelectionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already in dashboard
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.nav_rent) {
            startActivity(new Intent(this, RentalActivity.class));
        } else if (id == R.id.nav_return) {
            startActivity(new Intent(this, ReturnListActivity.class));
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (id == R.id.nav_location) {
            startActivity(new Intent(this, LocationSelectionActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            viewModel.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
