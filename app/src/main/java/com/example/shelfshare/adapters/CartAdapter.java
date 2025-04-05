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
import com.example.shelfshare.models.Book;
import com.example.shelfshare.utils.CartManager;

public class CartAdapter extends ListAdapter<Book, CartAdapter.CartViewHolder> {
    private final CartManager cartManager;

    public CartAdapter() {
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
        this.cartManager = CartManager.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Book book = getItem(position);
        holder.bind(book);
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBookCover;
        private final TextView tvBookTitle;
        private final TextView tvAuthor;
        private final TextView tvPrice;
        private final TextView tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);

            itemView.findViewById(R.id.btnRemove).setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Book book = getItem(position);
                    cartManager.removeFromCart(book);
                    notifyItemRemoved(position);
                }
            });

            itemView.findViewById(R.id.btnIncrease).setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Book book = getItem(position);
                    cartManager.increaseQuantity(book);
                    notifyItemChanged(position);
                }
            });

            itemView.findViewById(R.id.btnDecrease).setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Book book = getItem(position);
                    cartManager.decreaseQuantity(book);
                    notifyItemChanged(position);
                }
            });
        }

        public void bind(Book book) {
            tvBookTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvPrice.setText(String.format("₹%.2f/day", book.getPrice()));
            tvQuantity.setText(String.valueOf(cartManager.getQuantity(book)));

            Glide.with(itemView.getContext())
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivBookCover);
        }
    }
} 