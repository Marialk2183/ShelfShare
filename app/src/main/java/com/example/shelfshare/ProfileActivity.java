package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.databinding.ActivityProfileBinding;
import com.example.shelfshare.dialogs.ChangePasswordDialog;
import com.example.shelfshare.viewmodels.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupToolbar();
        setupClickListeners();
        setupObservers();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        binding.btnUpdateProfile.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String location = binding.etLocation.getText().toString().trim();

            if (name.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.updateProfile(name, location);
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog();
            dialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
        });
    }

    private void setupObservers() {
        viewModel.getUserName().observe(this, name -> {
            binding.etName.setText(name);
        });

        viewModel.getUserEmail().observe(this, email -> {
            binding.etEmail.setText(email);
        });

        viewModel.getUserLocation().observe(this, location -> {
            binding.etLocation.setText(location);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnUpdateProfile.setEnabled(!isLoading);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
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
