package com.example.shelfshare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shelfshare.R;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.utils.CartManager;
import com.example.shelfshare.utils.FavoritesManager;
import com.example.shelfshare.models.Book;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class BookAdapter extends ListAdapter<Book, BookAdapter.BookViewHolder> {
    private final OnBookClickListener listener;
    private CartManager cartManager;
    private FavoritesManager favoritesManager;

    public BookAdapter(OnBookClickListener listener) {
        super(new DiffUtil.ItemCallback<Book>() {
            @Override
            public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
        this.cartManager = CartManager.getInstance();
        this.favoritesManager = FavoritesManager.getInstance();
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
        Book book = getItem(position);
        holder.bind(book, listener);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBookCover;
        private final TextView tvBookTitle;
        private final TextView tvAuthor;
        private final TextView tvPrice;
        private final TextView tvLocation;
        private final RatingBar ratingBar;
        private final ImageButton btnFavorite;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnFavorite = itemView.findViewById(R.id.btnFavorites);

            itemView.findViewById(R.id.btnRent).setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Book book = getItem(position);
                    cartManager.addToCart(book);
                }
            });

            btnFavorite.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Book book = getItem(position);
                    if (favoritesManager.isFavorite(book)) {
                        favoritesManager.removeFromFavorites(book);
                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                    } else {
                        favoritesManager.addToFavorites(book);
                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                    }
                }
            });
        }

        public void bind(Book book, OnBookClickListener listener) {
            tvBookTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvPrice.setText(String.format("₹%.2f/day", book.getPrice()));
            tvLocation.setText(book.getLocation());
            ratingBar.setRating(book.getRating());

            // Load book cover image
            Glide.with(itemView.getContext())
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivBookCover);

            itemView.setOnClickListener(v -> listener.onBookClick(book));
        }
    }

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }
} 