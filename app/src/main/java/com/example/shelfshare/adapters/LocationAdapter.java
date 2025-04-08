package com.example.shelfshare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.R;
import com.example.shelfshare.models.Location;

public class LocationAdapter extends ListAdapter<Location, LocationAdapter.LocationViewHolder> {
    private final OnLocationClickListener listener;

    public interface OnLocationClickListener {
        void onLocationClick(Location location);
    }

    public LocationAdapter(OnLocationClickListener listener) {
        super(new DiffUtil.ItemCallback<Location>() {
            @Override
            public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                       oldItem.getAddress().equals(newItem.getAddress());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = getItem(position);
        holder.bind(location, listener);
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.locationNameTextView);
            addressTextView = itemView.findViewById(R.id.locationAddressTextView);
        }

        public void bind(final Location location, final OnLocationClickListener listener) {
            nameTextView.setText(location.getName());
            addressTextView.setText(location.getAddress());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLocationClick(location);
                }
            });
        }
    }
} 