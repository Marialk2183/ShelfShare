package com.example.shelfshare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shelfshare.R;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.utils.CartManager;
import com.example.shelfshare.utils.FavoritesManager;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<BookEntity> books;
    private CartManager cartManager;
    private FavoritesManager favoritesManager;

    public BookAdapter(List<BookEntity> books) {
        this.books = books;
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
        BookEntity book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateBooks(List<BookEntity> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBookCover;
        private TextView tvBookTitle;
        private TextView tvBookAuthor;
        private TextView tvBookPrice;
        private TextView tvBookLocation;
        private ImageButton btnFavorite;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
            tvBookLocation = itemView.findViewById(R.id.tvBookLocation);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            itemView.findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    BookEntity book = books.get(position);
                    cartManager.addToCart(book);
                }
            });

            btnFavorite.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    BookEntity book = books.get(position);
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

        public void bind(BookEntity book) {
            tvBookTitle.setText(book.getTitle());
            tvBookAuthor.setText(book.getAuthor());
            tvBookPrice.setText(String.format("$%.2f", book.getPrice()));
            tvBookLocation.setText(book.getLocation());
            
            // Load book cover image using Glide
            if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivBookCover);
            } else {
                ivBookCover.setImageResource(R.drawable.ic_launcher_foreground);
            }
            
            // Set favorite status
            btnFavorite.setImageResource(
                favoritesManager.isFavorite(book) 
                    ? android.R.drawable.btn_star_big_on 
                    : android.R.drawable.btn_star_big_off
            );
        }
    }
} 