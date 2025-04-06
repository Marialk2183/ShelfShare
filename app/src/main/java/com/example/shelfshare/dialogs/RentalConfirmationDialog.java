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
import com.example.shelfshare.data.BookEntity;

public class RentalConfirmationDialog extends DialogFragment {
    private static final String ARG_BOOK = "book";
    private static final String ARG_DAYS = "days";
    private static final String ARG_PRICE = "price";

    private OnRentalConfirmedListener listener;

    public interface OnRentalConfirmedListener {
        void onRentalConfirmed(BookEntity book, int days, double price);
    }

    public static RentalConfirmationDialog newInstance(BookEntity book, int days, double price) {
        RentalConfirmationDialog dialog = new RentalConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK, book.getId());
        args.putInt(ARG_DAYS, days);
        args.putDouble(ARG_PRICE, price);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnRentalConfirmedListener(OnRentalConfirmedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rental_confirmation, null);

        TextView tvBookTitle = view.findViewById(R.id.tvBookTitle);
        TextView tvRentalPeriod = view.findViewById(R.id.tvRentalPeriod);
        TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        Bundle args = getArguments();
        if (args != null) {
            String bookId = args.getString(ARG_BOOK);
            int days = args.getInt(ARG_DAYS);
            double price = args.getDouble(ARG_PRICE);

            tvBookTitle.setText(bookId); // You might want to load the actual book title
            tvRentalPeriod.setText(getString(R.string.rental_period, days));
            tvTotalPrice.setText(getString(R.string.total_price, price));
        }

        builder.setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    if (listener != null && args != null) {
                        BookEntity book = new BookEntity();
                        book.setId(args.getString(ARG_BOOK));
                        listener.onRentalConfirmed(
                            book,
                            args.getInt(ARG_DAYS),
                            args.getDouble(ARG_PRICE)
                        );
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
} 