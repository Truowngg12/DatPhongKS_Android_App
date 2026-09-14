package com.example.datphongks.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datphongks.data.models.Room;
import com.example.datphongks.databinding.ItemRoomBinding;

public class RoomAdapter extends ListAdapter<Room, RoomAdapter.RoomViewHolder> {

    private final OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(OnRoomClickListener listener) {
        super(new DiffUtil.ItemCallback<Room>() {
            @Override
            public boolean areItemsTheSame(@NonNull Room oldItem, @NonNull Room newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Room oldItem, @NonNull Room newItem) {
                return oldItem.getType().equals(newItem.getType()) && oldItem.getPricePerNight() == newItem.getPricePerNight();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(
                ItemRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoomBinding binding;

        public RoomViewHolder(ItemRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Room room, OnRoomClickListener listener) {
            binding.tvRoomType.setText(room.getType());
            binding.tvRoomDescription.setText(room.getDescription());
            binding.tvPricePerNight.setText("$" + room.getPricePerNight() + "/night");
            binding.tvCapacity.setText(room.getCapacity() + " Guests");

            Glide.with(binding.ivRoom.getContext())
                    .load(room.getImageUrl())
                    .centerCrop()
                    .into(binding.ivRoom);

            binding.btnSelectRoom.setOnClickListener(v -> listener.onRoomClick(room));
        }
    }
}