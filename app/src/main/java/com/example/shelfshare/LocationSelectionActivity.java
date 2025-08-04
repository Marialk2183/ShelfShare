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

import com.example.shelfshare.adapters.LocationAdapter;
import com.example.shelfshare.models.Location;
import com.example.shelfshare.viewmodels.LocationViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class LocationSelectionActivity extends AppCompatActivity {
    private LocationViewModel viewModel;
    private RecyclerView rvLocations;
    private CircularProgressIndicator progressBar;
    private TextView tvCurrentLocation;
    private LocationAdapter locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupObservers();

        // Load locations
        viewModel.loadLocations();
    }

    private void initializeViews() {
        rvLocations = findViewById(R.id.rvLocations);
        progressBar = findViewById(R.id.progressBar);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Select Location");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        locationAdapter = new LocationAdapter(location -> {
            // Handle location selection
            viewModel.selectLocation(location);
            finish();
        });
        rvLocations.setLayoutManager(new LinearLayoutManager(this));
        rvLocations.setAdapter(locationAdapter);
    }

    private void setupObservers() {
        viewModel.getLocations().observe(this, locations -> {
            locationAdapter.submitList(locations);
        });

        viewModel.getCurrentLocation().observe(this, location -> {
            if (location != null) {
                tvCurrentLocation.setText("Current Location: " + location.getName());
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            rvLocations.setVisibility(isLoading ? View.GONE : View.VISIBLE);
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