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

public class ReturnAdapter extends ListAdapter<Rental, ReturnAdapter.ReturnViewHolder> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public ReturnAdapter() {
        super(new DiffUtil.ItemCallback<Rental>() {
            @Override
            public boolean areItemsTheSame(@NonNull Rental oldItem, @NonNull Rental newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Rental oldItem, @NonNull Rental newItem) {
                return oldItem.getId().equals(newItem.getId());
            }
        });
    }

    @NonNull
    @Override
    public ReturnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_return, parent, false);
        return new ReturnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnViewHolder holder, int position) {
        Rental rental = getItem(position);
        holder.bind(rental);
    }

    public static class ReturnViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBookCover;
        private final TextView tvBookTitle;
        private final TextView tvAuthor;
        private final TextView tvRentalPeriod;
        private final TextView tvLateFee;
        private final TextView tvStatus;

        public ReturnViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvRentalPeriod = itemView.findViewById(R.id.tvRentalPeriod);
            tvLateFee = itemView.findViewById(R.id.tvLateFee);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Rental rental) {
            tvBookTitle.setText(rental.getBook().getTitle());
            tvAuthor.setText(rental.getBook().getAuthor());
            
            String startDate = dateFormat.format(rental.getStartDate());
            String endDate = dateFormat.format(rental.getEndDate());
            tvRentalPeriod.setText(String.format("%s - %s", startDate, endDate));
            
            double lateFee = rental.calculateLateFee();
            tvLateFee.setText(String.format("₹%.2f", lateFee));
            
            tvStatus.setText(rental.getStatus());

            Glide.with(itemView.getContext())
                    .load(rental.getBook().getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivBookCover);
        }
    }
} 