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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class FavoritesAdapter extends ListAdapter<BookEntity, FavoritesAdapter.FavoritesViewHolder> {
    private final OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(BookEntity book);
        void onRemoveClicked(BookEntity book);
    }

    public FavoritesAdapter(OnFavoriteClickListener listener) {
        super(new DiffUtil.ItemCallback<BookEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull BookEntity oldItem, @NonNull BookEntity newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull BookEntity oldItem, @NonNull BookEntity newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        BookEntity book = getItem(position);
        holder.bind(book, listener);
    }

    static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBookCover;
        private final TextView tvTitle;
        private final TextView tvAuthor;
        private final ImageButton btnRemove;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(final BookEntity book, final OnFavoriteClickListener listener) {
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            
            if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivBookCover);
            }

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClicked(book);
                }
            });
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(book);
                }
            });
        }
    }
} 