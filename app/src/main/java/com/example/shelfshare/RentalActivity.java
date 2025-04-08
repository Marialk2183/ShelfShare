package com.example.shelfshare;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.adapters.RentalAdapter;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.data.Rental;
import com.example.shelfshare.dialogs.RentalConfirmationDialog;
import com.example.shelfshare.viewmodels.RentalViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;

public class RentalActivity extends AppCompatActivity implements RentalAdapter.OnRentalClickListener, RentalConfirmationDialog.OnRentalConfirmedListener {
    private RentalViewModel viewModel;
    private RentalAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Rentals");
        }

        progressBar = findViewById(R.id.progressBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RentalAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(RentalViewModel.class);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getRentals().observe(this, rentals -> {
            adapter.submitList(rentals);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadRentals();
    }

    @Override
    public void onRentalClick(Rental rental) {
        // Handle rental click, e.g., show rental details
        Toast.makeText(this, "Rental clicked: " + rental.getBookTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRentalConfirmed(BookEntity book, int days, double price) {
        viewModel.createRental(book, days, price);
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