package com.example.datphongks.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.datphongks.R;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.databinding.ItemHotelBinding;
import com.example.datphongks.simplebooking.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<Hotel> favoriteList;
    private OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel, View imageView);
    }

    public FavoriteAdapter(List<Hotel> favoriteList, OnHotelClickListener listener) {
        this.favoriteList = favoriteList != null ? favoriteList : new ArrayList<>();
        this.listener = listener;
    }

    public void setFavoriteList(List<Hotel> favoriteList) {
        this.favoriteList = favoriteList != null ? favoriteList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(
                ItemHotelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Hotel hotel = favoriteList.get(position);
        holder.bind(hotel);

        // Click trái tim -> Hủy yêu thích & xóa khỏi danh sách mượt mà
        holder.binding.btnFavorite.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && adapterPos < favoriteList.size()) {
                Hotel item = favoriteList.get(adapterPos);

                // 1. Xóa khỏi SQLite qua DatabaseHelper
                DatabaseHelper dbHelper = new DatabaseHelper(v.getContext());
                dbHelper.removeFavorite(Integer.parseInt(item.getId()));

                // 2. Xóa khỏi danh sách favoriteList
                favoriteList.remove(adapterPos);

                // 3. Cập nhật giao diện mượt mà (không dùng notifyDataSetChanged)
                notifyItemRemoved(adapterPos);
                notifyItemRangeChanged(adapterPos, favoriteList.size());
            }
        });

        // Click item -> Chi tiết phòng
        holder.binding.getRoot().setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && listener != null) {
                listener.onHotelClick(favoriteList.get(adapterPos), holder.binding.ivHotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        final ItemHotelBinding binding;

        public FavoriteViewHolder(ItemHotelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Hotel hotel) {
            binding.tvHotelName.setText(hotel.getName());
            binding.tvLocation.setText(hotel.getLocation());
            binding.tvRating.setText(String.valueOf(hotel.getRating()));
            binding.tvPrice.setText(hotel.getPricePerNight() + " VND");

            Glide.with(binding.ivHotel.getContext())
                    .load(hotel.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivHotel);

            // Vì ở trang Favorites nên biểu tượng luôn là tim đỏ
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled);
        }
    }
}
