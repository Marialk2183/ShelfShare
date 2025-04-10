package com.example.shelfshare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    public interface OnBookClickListener {
        void onBookClick(Book book);

        void onBookClick(BookEntity book);
        void onAddToCartClick(BookEntity book);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(BookEntity book);
    }

    private List<BookEntity> books;
    private OnBookClickListener listener;
    private OnFavoriteClickListener favoriteListener;

    public BookAdapter(List<BookEntity> books, OnBookClickListener listener) {
        this.books = books;
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteListener = listener;
    }

    public List<BookEntity> getBooks() {
        return books;
    }

    public void setBooks(List<BookEntity> books) {
        this.books = books;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookEntity book = books.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.priceTextView.setText(String.format("₹%.2f", book.getPrice()));

        // Load book image
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(book.getImageUrl())
                .placeholder(R.drawable.default_book_cover)
                .error(R.drawable.default_book_cover)
                .into(holder.bookImageView);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });

        holder.addToCartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCartClick(book);
            }
        });

        // Handle favorite button if available
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(book);
                }
            });
            holder.btnFavorite.setSelected(book.isFavorite());
        }
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : 0;
    }

    public void updateBooks(List<BookEntity> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookBinding binding;
        private final ImageView bookImageView;
        private final TextView titleTextView;
        private final TextView authorTextView;
        private final TextView priceTextView;
        private final Button addToCartButton;
        private final ImageButton btnFavorite;

        ViewHolder(View itemView) {
            super(itemView);
            binding = ItemBookBinding.bind(itemView);
            bookImageView = binding.ivBook;
            titleTextView = binding.tvTitle;
            authorTextView = binding.tvAuthor;
            priceTextView = binding.tvPrice;
            addToCartButton = binding.btnAddToCart;
            btnFavorite = binding.btnFavorite;
        }
    }
}
