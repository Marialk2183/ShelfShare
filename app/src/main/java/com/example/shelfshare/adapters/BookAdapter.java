package com.example.shelfshare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shelfshare.R;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.databinding.ItemBookBinding;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    public interface OnBookClickListener {
        void onBookClick(BookEntity book);
        void onAddToCartClick(BookEntity book);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(BookEntity book);
    }

    private final OnBookClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    private List<BookEntity> books;

    public BookAdapter(OnBookClickListener listener) {
        this.listener = listener;
        this.books = new ArrayList<>();
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteListener = listener;
    }

    public void updateBooks(List<BookEntity> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    public List<BookEntity> getBooks() {
        return new ArrayList<>(books);
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookBinding binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.getContext()), 
            parent, 
            false
        );
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bind(books.get(position));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookBinding binding;

        BookViewHolder(ItemBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BookEntity book) {
            binding.tvTitle.setText(book.getTitle());
            binding.tvAuthor.setText(book.getAuthor());
            binding.tvPrice.setText(String.format("₹%.2f", book.getPrice()));

            // Load image if available
            if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(book.getImageUrl())
                    .into(binding.ivBook);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(book);
                }
            });

            binding.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(book);
                }
            });

            // Handle favorite button if available
            if (binding.btnFavorite != null) {
                binding.btnFavorite.setOnClickListener(v -> {
                    if (favoriteListener != null) {
                        favoriteListener.onFavoriteClick(book);
                    }
                });
                binding.btnFavorite.setSelected(book.isFavorite());
            }
        }
    }
}
