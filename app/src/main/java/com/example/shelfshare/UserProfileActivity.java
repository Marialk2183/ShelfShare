package com.example.shelfshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.shelfshare.dialogs.ChangePasswordDialog;
import com.example.shelfshare.databinding.ActivityUserProfileBinding;
import com.example.shelfshare.viewmodels.UserProfileViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;
    private UserProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        viewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        setupObservers();
        setupClickListeners();

        binding.ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                binding.etName.setText(user.getName());
                binding.etEmail.setText(user.getEmail());
            }
        });

        viewModel.getProfileImage().observe(this, uri -> {
            if (uri != null) {
                binding.ivProfile.setImageURI(uri);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
            binding.btnChangePassword.setEnabled(!isLoading);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.updateProfile(name, email);
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        TextInputEditText etNewPassword = view.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        builder.setView(view)
            .setTitle("Change Password")
            .setPositiveButton("Change", (dialog, which) -> {
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (newPassword.equals(confirmPassword)) {
                    viewModel.changePassword(currentPassword, newPassword);
                } else {
                    Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                viewModel.updateProfileImage(uri);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 