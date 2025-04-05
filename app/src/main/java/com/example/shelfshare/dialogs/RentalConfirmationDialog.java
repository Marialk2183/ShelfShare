package com.example.shelfshare.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.shelfshare.R;
import com.example.shelfshare.models.Rental;
import com.example.shelfshare.viewmodels.RentalViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RentalConfirmationDialog extends DialogFragment {
    private RentalViewModel viewModel;
    private Rental rental;
    private TextView tvBookTitle, tvRentalPeriod, tvTotalAmount;

    public RentalConfirmationDialog(Rental rental) {
        this.rental = rental;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rental_confirmation, null);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(RentalViewModel.class);

        // Initialize views
        initializeViews(view);
        updateUI();

        builder.setView(view)
                .setTitle("Confirm Rental")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    viewModel.confirmRental(rental);
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void initializeViews(View view) {
        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        tvRentalPeriod = view.findViewById(R.id.tvRentalPeriod);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
    }

    private void updateUI() {
        tvBookTitle.setText(rental.getBookTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String startDate = dateFormat.format(rental.getStartDate());
        String endDate = dateFormat.format(rental.getEndDate());
        tvRentalPeriod.setText(String.format("%s to %s", startDate, endDate));

        double totalAmount = rental.getDailyRate() * rental.getDuration();
        tvTotalAmount.setText(String.format("₹%.2f", totalAmount));
    }
} 