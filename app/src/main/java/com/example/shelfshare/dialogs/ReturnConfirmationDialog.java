package com.example.shelfshare.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.R;
import com.example.shelfshare.models.Rental;
import com.example.shelfshare.viewmodels.ReturnListViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReturnConfirmationDialog extends DialogFragment {
    private ReturnListViewModel viewModel;
    private Rental rental;
    private TextView tvBookTitle, tvRentalPeriod, tvReturnDate, tvLateFee;

    public ReturnConfirmationDialog(Rental rental) {
        this.rental = rental;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_return_confirmation, null);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ReturnListViewModel.class);

        // Initialize views
        initializeViews(view);
        updateUI();

        builder.setView(view)
                .setTitle("Confirm Return")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    viewModel.confirmReturn(rental);
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void initializeViews(View view) {
        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        tvRentalPeriod = view.findViewById(R.id.tvRentalPeriod);
        tvReturnDate = view.findViewById(R.id.tvReturnDate);
        tvLateFee = view.findViewById(R.id.tvLateFee);
    }

    private void updateUI() {
        tvBookTitle.setText(rental.getBookTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String startDate = dateFormat.format(rental.getStartDate());
        String endDate = dateFormat.format(rental.getEndDate());
        tvRentalPeriod.setText(String.format("%s to %s", startDate, endDate));

        String returnDate = dateFormat.format(rental.getReturnDate());
        tvReturnDate.setText(returnDate);

        double lateFee = rental.calculateLateFee();
        if (lateFee > 0) {
            tvLateFee.setText(String.format("Late Fee: ₹%.2f", lateFee));
            tvLateFee.setVisibility(View.VISIBLE);
        } else {
            tvLateFee.setVisibility(View.GONE);
        }
    }
} 