package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.viewmodels.SettingsViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    private SettingsViewModel viewModel;
    private SwitchMaterial switchNotifications, switchDarkMode;
    private TextView tvVersion, tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupClickListeners();
        setupObservers();

        // Load settings
        viewModel.loadSettings();
    }

    private void initializeViews() {
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        tvVersion = findViewById(R.id.tvVersion);
        tvLogout = findViewById(R.id.tvLogout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setNotificationsEnabled(isChecked);
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setDarkModeEnabled(isChecked);
        });

        tvLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }

    private void setupObservers() {
        viewModel.getNotificationsEnabled().observe(this, enabled -> {
            switchNotifications.setChecked(enabled);
        });

        viewModel.getDarkModeEnabled().observe(this, enabled -> {
            switchDarkMode.setChecked(enabled);
        });

        viewModel.getAppVersion().observe(this, version -> {
            tvVersion.setText("Version " + version);
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