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
import com.example.shelfshare.data.Location;
import java.util.List;

public class LocationAdapter extends ListAdapter<Location, LocationAdapter.LocationViewHolder> {
    private final OnLocationClickListener listener;

    public LocationAdapter(OnLocationClickListener listener) {
        super(new DiffUtil.ItemCallback<Location>() {
            @Override
            public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
                return oldItem.equals(newItem);
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
        holder.bind(getItem(position));
    }

    public void submitList(List<Location> locations) {
        super.submitList(locations);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLocationName;
        private final TextView tvLocationAddress;

        LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvLocationAddress = itemView.findViewById(R.id.tvLocationAddress);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onLocationClick(getItem(position));
                }
            });
        }

        void bind(Location location) {
            tvLocationName.setText(location.getName());
            tvLocationAddress.setText(location.getAddress());
        }
    }

    public interface OnLocationClickListener {
        void onLocationClick(Location location);
    }
} 