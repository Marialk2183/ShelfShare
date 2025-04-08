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
import com.example.shelfshare.databinding.ContentDashboardBinding;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.models.Location;
import com.example.shelfshare.viewmodels.DashboardViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import de.hdodenhof.circleimageview.CircleImageView;
import com.example.shelfshare.viewmodels.CartViewModel;

public class DashboardActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private ActivityDashboardBinding binding;
    private ContentDashboardBinding contentBinding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DashboardViewModel viewModel;
    private BookAdapter adapter;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views using view binding
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        Toolbar toolbar = binding.toolbar;

        setupToolbar(toolbar);
        setupNavigationDrawer(toolbar);
        setupUserInfo();
        setupCardClickListeners();
        observeViewModel();
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("Dashboard");
        }
    }

    private void setupCardClickListeners() {
        // Get the included layout binding
        contentBinding = ContentDashboardBinding.bind(binding.getRoot().findViewById(R.id.content_dashboard));
        
        // Available Books Card
        contentBinding.cardAvailableBooks.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, AvailableBooksActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Error opening Available Books", Toast.LENGTH_SHORT).show();
            }
        });

        // My Books Card
        contentBinding.cardMyBooks.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, MyBooksActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Error opening My Books", Toast.LENGTH_SHORT).show();
            }
        });

        // Add Book Card
        contentBinding.cardAddBook.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, AddBookActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Error opening Add Book", Toast.LENGTH_SHORT).show();
            }
        });

        // Favorites Card
        contentBinding.cardFavorites.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, FavoritesActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Error opening Favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Profile Card
        contentBinding.cardProfile.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, UserProfileActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Error opening Profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Cart Card
        contentBinding.cardCart.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, CartActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Error opening Cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigationDrawer(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            try {
                if (itemId == R.id.nav_my_books) {
                    startActivity(new Intent(this, MyBooksActivity.class));
                } else if (itemId == R.id.nav_add_book) {
                    startActivity(new Intent(this, AddBookActivity.class));
                } else if (itemId == R.id.nav_available_books) {
                    startActivity(new Intent(this, AvailableBooksActivity.class));
                } else if (itemId == R.id.nav_settings) {
                    startActivity(new Intent(this, SettingsActivity.class));
                } else if (itemId == R.id.nav_logout) {
                    viewModel.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error navigating to selected item", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupUserInfo() {
        try {
            View headerView = navigationView.getHeaderView(0);
            TextView usernameTextView = headerView.findViewById(R.id.nav_username);
            TextView emailTextView = headerView.findViewById(R.id.nav_user_email);
            CircleImageView userImageView = headerView.findViewById(R.id.nav_user_image);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String displayName = currentUser.getDisplayName();
                String email = currentUser.getEmail();

                if (displayName != null && !displayName.isEmpty()) {
                    usernameTextView.setText(displayName);
                }
                if (email != null && !email.isEmpty()) {
                    emailTextView.setText(email);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up user info", Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(this, user -> {
            try {
                if (user != null) {
                    View headerView = navigationView.getHeaderView(0);
                    TextView usernameTextView = headerView.findViewById(R.id.nav_username);
                    TextView emailTextView = headerView.findViewById(R.id.nav_user_email);

                    String displayName = user.getDisplayName();
                    String email = user.getEmail();

                    if (displayName != null && !displayName.isEmpty()) {
                        usernameTextView.setText(displayName);
                    }
                    if (email != null && !email.isEmpty()) {
                        emailTextView.setText(email);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error updating user info", Toast.LENGTH_SHORT).show();
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
        try {
            Intent intent = new Intent(this, BookDetailsActivity.class);
            intent.putExtra("book_id", book.getId());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening book details", Toast.LENGTH_SHORT).show();
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
