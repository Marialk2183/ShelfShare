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
import com.example.shelfshare.data.BookEntity;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends ListAdapter<BookEntity, BookAdapter.BookViewHolder> {
    private final OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(BookEntity book);
    }

    public BookAdapter(OnBookClickListener listener) {
        super(new DiffUtil.ItemCallback<BookEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull BookEntity oldItem, @NonNull BookEntity newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull BookEntity oldItem, @NonNull BookEntity newItem) {
                return oldItem.getId().equals(newItem.getId());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookEntity book = getItem(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.priceTextView.setText(String.format("$%.2f/day", book.getPrice()));
        holder.availabilityTextView.setText(book.isAvailable() ? "Available" : "Not Available");
        
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        TextView priceTextView;
        TextView availabilityTextView;

        BookViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textBookTitle);
            authorTextView = itemView.findViewById(R.id.textBookAuthor);
            priceTextView = itemView.findViewById(R.id.textBookPrice);
            availabilityTextView = itemView.findViewById(R.id.textBookAvailability);
        }
    }
} 