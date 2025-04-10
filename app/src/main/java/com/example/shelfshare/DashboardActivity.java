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
import androidx.recyclerview.widget.GridLayoutManager;
import com.bumptech.glide.Glide;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class DashboardActivity extends AppCompatActivity 
    implements NavigationView.OnNavigationItemSelectedListener, 
               BookAdapter.OnBookClickListener {
    private ActivityDashboardBinding binding;
    private ContentDashboardBinding contentBinding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DashboardViewModel viewModel;
    private BookAdapter adapter;
    private CartViewModel cartViewModel;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private List<BookEntity> books;

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

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        books = new ArrayList<>();

        setupToolbar(toolbar);
        setupNavigationDrawer(toolbar);
        setupUserInfo();
        setupCardClickListeners();
        observeViewModel();
        setupRecyclerView();
        loadUserProfile();
        loadBooks();
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

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupUserInfo() {
        try {
            View headerView = navigationView.getHeaderView(0);
            TextView usernameTextView = headerView.findViewById(R.id.nav_username);
            TextView emailTextView = headerView.findViewById(R.id.nav_user_email);
            CircleImageView userImageView = headerView.findViewById(R.id.nav_header_image);

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

    private void setupRecyclerView() {
        adapter = new BookAdapter(books, this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadUserProfile() {
        if (auth.getCurrentUser() != null) {
            db.collection("users").document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Update UI with user data
                        String name = document.getString("name");
                        binding.welcomeText.setText("Welcome, " + name);
                    }
                });
        }
    }

    private void loadBooks() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("books")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                books.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    BookEntity book = document.toObject(BookEntity.class);
                    if (book != null) {
                        books.add(book);
                    }
                }
                adapter.updateBooks(books);
                binding.progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error loading books: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
        } else if (id == R.id.nav_my_books) {
            intent = new Intent(this, MyBooksActivity.class);
        } else if (id == R.id.nav_favorites) {
            intent = new Intent(this, FavoritesActivity.class);
        } else if (id == R.id.nav_cart) {
            intent = new Intent(this, CartActivity.class);
        } else if (id == R.id.nav_logout) {
            auth.signOut();
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        if (intent != null) {
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book", (Serializable) book);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(BookEntity book) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
            .collection("cart")
            .document(book.getId())
            .set(book)
            .addOnSuccessListener(aVoid -> 
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> 
                Toast.makeText(this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
