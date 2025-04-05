package com.example.shelfshare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shelfshare.R;
import com.example.shelfshare.models.Rental;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RentalAdapter extends ListAdapter<Rental, RentalAdapter.RentalViewHolder> {
    private final OnRentalClickListener listener;

    public RentalAdapter(OnRentalClickListener listener) {
        super(new DiffUtil.ItemCallback<Rental>() {
            @Override
            public boolean areItemsTheSame(@NonNull Rental oldItem, @NonNull Rental newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Rental oldItem, @NonNull Rental newItem) {
                return oldItem.equals(newItem);
            }
        });
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
        Rental rental = getItem(position);
        holder.bind(rental, listener);
    }

    static class RentalViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBookCover;
        private final TextView tvBookTitle;
        private final TextView tvRentalPeriod;
        private final TextView tvTotalAmount;
        private final TextView tvStatus;

        public RentalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvRentalPeriod = itemView.findViewById(R.id.tvRentalPeriod);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Rental rental, OnRentalClickListener listener) {
            tvBookTitle.setText(rental.getBookTitle());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String startDate = dateFormat.format(rental.getStartDate());
            String endDate = dateFormat.format(rental.getEndDate());
            tvRentalPeriod.setText(String.format("%s to %s", startDate, endDate));
            
            tvTotalAmount.setText(String.format("₹%.2f", rental.getTotalAmount()));
            tvStatus.setText(rental.getStatus());

            Glide.with(itemView.getContext())
                    .load(rental.getBookImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivBookCover);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRentalClick(rental);
                }
            });
        }
    }

    public interface OnRentalClickListener {
        void onRentalClick(Rental rental);
    }
} 