package com.example.datphongks.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.datphongks.R;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.databinding.ItemHotelBinding;

public class HotelAdapter extends ListAdapter<Hotel, HotelAdapter.HotelViewHolder> {

    private final OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel, View imageView);
        void onFavoriteClick(Hotel hotel);
    }

    public HotelAdapter(OnHotelClickListener listener) {
        super(new DiffUtil.ItemCallback<Hotel>() {
            @Override
            public boolean areItemsTheSame(@NonNull Hotel oldItem, @NonNull Hotel newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull Hotel oldItem, @NonNull Hotel newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HotelViewHolder(
                ItemHotelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = getItem(position);
        holder.bind(hotel);

        // 1. Sự kiện Click độc lập cho nút Trái tim (Thả/Hủy tim)
        holder.binding.btnFavorite.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Hotel currentHotel = getItem(pos);
                boolean newStatus = !currentHotel.isFavorite();
                currentHotel.setFavorite(newStatus);

                // Cập nhật icon ngay lập tức trên UI mượt mà
                holder.binding.btnFavorite.setImageResource(
                        newStatus ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
                );

                // Giao việc lưu Database cho Fragment/Activity xử lý qua Listener
                if (listener != null) {
                    listener.onFavoriteClick(currentHotel);
                }
            }
        });

        // 2. Sự kiện Click độc lập cho toàn bộ thẻ phòng (Xem chi tiết)
        holder.binding.getRoot().setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                Hotel currentHotel = getItem(pos);
                listener.onHotelClick(currentHotel, holder.binding.ivHotel);
            }
        });
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        final ItemHotelBinding binding;

        public HotelViewHolder(ItemHotelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Hotel hotel) {
            binding.tvHotelName.setText(hotel.getName());
            binding.tvLocation.setText(hotel.getLocation());
            binding.tvRating.setText(String.valueOf(hotel.getRating()));
            binding.tvPrice.setText(hotel.getPricePerNight() + " VND");

            ViewCompat.setTransitionName(binding.ivHotel, "hotel_image_" + hotel.getId());

            Glide.with(binding.ivHotel.getContext())
                    .load(hotel.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivHotel);

            // Tự động đổi màu tim dựa trên trạng thái dữ liệu (chỉ cần gọi ở đây là đủ)
            binding.btnFavorite.setImageResource(
                    hotel.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
            );
        }
    }
}