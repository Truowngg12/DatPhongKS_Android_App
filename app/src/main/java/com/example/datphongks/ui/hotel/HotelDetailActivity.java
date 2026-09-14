package com.example.datphongks.ui.hotel;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.datphongks.databinding.ActivityHotelDetailBinding;
import com.example.datphongks.simplebooking.DatabaseHelper;
import com.example.datphongks.simplebooking.Room;
import com.example.datphongks.simplebooking.SimpleBookingActivity;

public class HotelDetailActivity extends AppCompatActivity {

    private ActivityHotelDetailBinding binding;
    private DatabaseHelper dbHelper;
    private Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupToolbar();
        loadDynamicHotelData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadDynamicHotelData() {
        Intent intent = getIntent();
        if (intent == null) return;

        // 1. Nhận ID phòng từ Intent
        String hotelIdStr = intent.getStringExtra("hotel_id");
        int roomId = -1;
        if (hotelIdStr != null) {
            try {
                roomId = Integer.parseInt(hotelIdStr);
            } catch (NumberFormatException ignored) {}
        }

        // 2. Truy vấn dữ liệu ĐỘNG từ SQLite
        if (roomId != -1) {
            currentRoom = dbHelper.getRoomById(roomId);
        }

        // 3. Binding dữ liệu động lên View (Xóa toàn bộ dữ liệu tĩnh/hardcode)
        if (currentRoom != null) {
            binding.tvHotelName.setText(currentRoom.getName());

            String location = (currentRoom.getLocation() != null && !currentRoom.getLocation().isEmpty())
                    ? currentRoom.getLocation() : "Hà Nội, Việt Nam";
            binding.tvLocation.setText(location);

            String description = (currentRoom.getDescription() != null && !currentRoom.getDescription().isEmpty())
                    ? currentRoom.getDescription() : "Tiện ích nổi bật: " + currentRoom.getAmenities();
            binding.tvDescription.setText(description);

            binding.btnBookNow.setText("ĐẶT PHÒNG NGAY (" + (int) currentRoom.getPrice() + " VNĐ)");

            String imageUrl = (currentRoom.getImageUrl() != null && !currentRoom.getImageUrl().isEmpty())
                    ? currentRoom.getImageUrl()
                    : "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80";

            // Nạp hình ảnh động bằng Glide vào ViewPager2 background
            Glide.with(this)
                    .asDrawable()
                    .load(imageUrl)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            binding.ivHotelSlider.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}
                    });

            final int finalRoomId = currentRoom.getId();
            final String finalRoomName = currentRoom.getName();
            final double finalRoomPrice = currentRoom.getPrice();

            binding.btnBookNow.setOnClickListener(v -> {
                Intent bookingIntent = new Intent(this, com.example.datphongks.ui.booking.BookingActivity.class);
                bookingIntent.putExtra("room_id", finalRoomId);
                bookingIntent.putExtra("room_name", finalRoomName);
                bookingIntent.putExtra("room_price", finalRoomPrice);
                startActivity(bookingIntent);
            });
        } else {
            // Nhận dữ liệu trực tiếp nếu Intent truyền đầy đủ extras
            String name = intent.getStringExtra("hotel_name");
            String location = intent.getStringExtra("hotel_location");
            String description = intent.getStringExtra("hotel_description");
            String imageUrl = intent.getStringExtra("hotel_image");
            double price = intent.getDoubleExtra("hotel_price", 0);

            if (name != null) binding.tvHotelName.setText(name);
            if (location != null) binding.tvLocation.setText(location);
            if (description != null) binding.tvDescription.setText(description);
            if (price > 0) binding.btnBookNow.setText("ĐẶT PHÒNG NGAY (" + (int) price + " VNĐ)");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .asDrawable()
                        .load(imageUrl)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                binding.ivHotelSlider.setBackground(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {}
                        });
            }
        }
    }
}
