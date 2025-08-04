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
import com.example.shelfshare.models.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                       oldItem.getImageUrl().equals(newItem.getImageUrl()) &&
                       oldItem.getBookCount() == newItem.getBookCount();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);
        holder.bind(category, listener);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView countTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.categoryImage);
            nameTextView = itemView.findViewById(R.id.categoryName);
            countTextView = itemView.findViewById(R.id.bookCount);
        }

        public void bind(Category category, OnCategoryClickListener listener) {
            nameTextView.setText(category.getName());
            countTextView.setText(category.getBookCount() + " Books");
            
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(category.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }

            itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }
} 