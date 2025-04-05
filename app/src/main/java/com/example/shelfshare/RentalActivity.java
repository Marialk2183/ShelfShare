package com.example.shelfshare;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shelfshare.adapters.RentalAdapter;
import com.example.shelfshare.models.Rental;
import com.example.shelfshare.viewmodels.RentalViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class RentalActivity extends AppCompatActivity {
    private RentalViewModel viewModel;
    private RecyclerView rvRentals;
    private CircularProgressIndicator progressBar;
    private TextView tvEmpty;
    private RentalAdapter rentalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RentalViewModel.class);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupObservers();

        // Load rentals
        viewModel.loadRentals();
    }

    private void initializeViews() {
        rvRentals = findViewById(R.id.rvRentals);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Rentals");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        rentalAdapter = new RentalAdapter(rental -> {
            // Handle rental click
            RentalConfirmationDialog dialog = new RentalConfirmationDialog(rental);
            dialog.show(getSupportFragmentManager(), "RentalConfirmationDialog");
        });
        rvRentals.setLayoutManager(new LinearLayoutManager(this));
        rvRentals.setAdapter(rentalAdapter);
    }

    private void setupObservers() {
        viewModel.getRentals().observe(this, rentals -> {
            rentalAdapter.submitList(rentals);
            tvEmpty.setVisibility(rentals.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            rvRentals.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 