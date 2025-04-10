package com.example.shelfshare.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.R;
import com.example.shelfshare.data.Rental;
import com.example.shelfshare.viewmodels.ReturnListViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReturnConfirmationDialog extends DialogFragment {
    private ReturnListViewModel viewModel;
    private Rental rental;
    private TextView tvBookTitle;
    private TextView tvReturnDate;
    private Button btnConfirm;
    private Button btnCancel;

    public ReturnConfirmationDialog(Rental rental) {
        this.rental = rental;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_return_confirmation, null);
        
        viewModel = new ViewModelProvider(requireActivity()).get(ReturnListViewModel.class);
        
        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        tvReturnDate = view.findViewById(R.id.tvReturnDate);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);

        tvBookTitle.setText(rental.getBookTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String returnDate = dateFormat.format(rental.getReturnDate());
        tvReturnDate.setText(returnDate);

        btnConfirm.setOnClickListener(v -> {
            viewModel.confirmReturn(rental);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
} 