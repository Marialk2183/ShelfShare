package com.example.shelfshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.utils.CartManager;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<BookEntity> cartItems;
    private OnCartItemChangedListener listener;
    private CartManager cartManager;

    public interface OnCartItemChangedListener {
        void onCartItemChanged();
    }

    public CartAdapter(List<BookEntity> cartItems, OnCartItemChangedListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
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
        BookEntity book = cartItems.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBookTitle;
        private TextView tvBookPrice;
        private ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(BookEntity book) {
            tvBookTitle.setText(book.getTitle());
            tvBookPrice.setText(String.format("$%.2f", book.getPrice()));

            btnRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    BookEntity removedBook = cartItems.get(position);
                    cartManager.removeFromCart(removedBook);
                    notifyItemRemoved(position);
                    if (listener != null) {
                        listener.onCartItemChanged();
                    }
                }
            });
        }
    }
} 