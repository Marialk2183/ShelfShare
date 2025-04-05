package com.example.shelfshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.shelfshare.viewmodels.UserProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {
    private UserProfileViewModel viewModel;
    private CircularProgressIndicator progressBar;
    private ImageView ivProfile;
    private EditText etName, etEmail, etPhone;
    private MaterialButton btnSave, btnChangePassword;
    private TextView tvJoinedDate;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupImagePicker();
        setupClickListeners();
        setupObservers();

        // Load user data
        viewModel.loadUserData();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        ivProfile = findViewById(R.id.ivProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        tvJoinedDate = findViewById(R.id.tvJoinedDate);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(ivProfile);
                }
            }
        );
    }

    private void setupClickListeners() {
        ivProfile.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            viewModel.updateProfile(name, phone, selectedImageUri);
        });

        btnChangePassword.setOnClickListener(v -> {
            // Show change password dialog
            ChangePasswordDialog dialog = new ChangePasswordDialog();
            dialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
        });
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                updateUI(user);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!isLoading);
        });

        viewModel.getUpdateSuccess().observe(this, success -> {
            if (success) {
                // Show success message
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        etName.setText(user.getDisplayName());
        etEmail.setText(user.getEmail());
        etEmail.setEnabled(false); // Email cannot be changed

        // Load profile image
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                .load(user.getPhotoUrl())
                .circleCrop()
                .into(ivProfile);
        }

        // Set joined date
        if (user.getMetadata() != null) {
            long creationTime = user.getMetadata().getCreationTimestamp();
            // Format and display creation time
        }
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