package com.example.shelfshare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.R;
import com.example.shelfshare.data.Rental;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.RentalViewHolder> {
    private List<Rental> rentals;
    private final OnRentalClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnRentalClickListener {
        void onRentalClick(Rental rental);
    }

    public RentalAdapter(List<Rental> rentals, OnRentalClickListener listener) {
        this.rentals = rentals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rental, parent, false);
        return new RentalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalViewHolder holder, int position) {
        Rental rental = rentals.get(position);
        holder.bind(rental);
    }

    @Override
    public int getItemCount() {
        return rentals != null ? rentals.size() : 0;
    }

    public void updateRentals(List<Rental> newRentals) {
        this.rentals = newRentals;
        notifyDataSetChanged();
    }

    class RentalViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBookTitle;
        private final TextView tvRentalPeriod;
        private final TextView tvStatus;
        private final TextView tvTotalPrice;

        RentalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvRentalPeriod = itemView.findViewById(R.id.tvRentalPeriod);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }

        void bind(Rental rental) {
            tvBookTitle.setText(rental.getBookTitle());
            tvRentalPeriod.setText(String.format("%s - %s",
                    dateFormat.format(rental.getStartDate()),
                    dateFormat.format(rental.getEndDate())));
            tvStatus.setText(rental.getStatus());
            tvTotalPrice.setText(String.format("$%.2f", rental.getTotalPrice()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRentalClick(rental);
                }
            });
        }
    }
} 