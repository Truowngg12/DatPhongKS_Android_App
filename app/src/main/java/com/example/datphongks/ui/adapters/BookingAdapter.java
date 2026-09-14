package com.example.datphongks.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datphongks.data.models.Booking;
import com.example.datphongks.databinding.ItemBookingBinding;

public class BookingAdapter extends ListAdapter<Booking, BookingAdapter.BookingViewHolder> {

    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingAdapter(OnBookingClickListener listener) {
        super(new DiffUtil.ItemCallback<Booking>() {
            @Override
            public boolean areItemsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
                return oldItem.getStatus().equals(newItem.getStatus());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingViewHolder(
                ItemBookingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingBinding binding;

        public BookingViewHolder(ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Booking booking, OnBookingClickListener listener) {
            binding.tvHotelName.setText(booking.getHotel().getName());
            binding.tvBookingDates.setText(booking.getCheckInDate() + " - " + booking.getCheckOutDate());
            binding.tvTotalPrice.setText("$" + booking.getTotalPrice());
            binding.tvStatus.setText(booking.getStatus());

            Glide.with(binding.ivHotelThumbnail.getContext())
                    .load(booking.getHotel().getImageUrl())
                    .centerCrop()
                    .into(binding.ivHotelThumbnail);

            binding.getRoot().setOnClickListener(v -> listener.onBookingClick(booking));
        }
    }
}
