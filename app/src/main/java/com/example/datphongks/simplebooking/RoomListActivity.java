package com.example.datphongks.simplebooking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.datphongks.R;

import java.util.List;
import java.util.Locale;

public class RoomListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView lvDanhSachPhong;
    private TextView tvRoomListTitle;
    private List<Room> roomList;
    private String filterStatus = "ALL";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        lvDanhSachPhong = findViewById(R.id.lvDanhSachPhong);
        tvRoomListTitle = findViewById(R.id.tvRoomListTitle);
        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("filter_status")) {
            filterStatus = intent.getStringExtra("filter_status");
        }

        updateTitle();

        roomList = databaseHelper.getRoomsByStatus(filterStatus);

        RoomAdapter roomAdapter = new RoomAdapter(this, roomList);
        lvDanhSachPhong.setAdapter(roomAdapter);

        // Bắt sự kiện click vào phòng
        lvDanhSachPhong.setOnItemClickListener((parent, view, position, id) -> {
            Room selectedRoom = roomList.get(position);
            showRoomOptionsDialog(selectedRoom);
        });
    }

    private void updateTitle() {
        if (tvRoomListTitle != null) {
            if ("Trống".equalsIgnoreCase(filterStatus)) {
                tvRoomListTitle.setText("Danh Sách Phòng Trống (Sẵn Sàng)");
            } else if ("Đã đặt".equalsIgnoreCase(filterStatus)) {
                tvRoomListTitle.setText("Danh Sách Phòng Đã Đặt (Có Khách)");
            } else {
                tvRoomListTitle.setText("Quản Lý Tất Cả Danh Sách Phòng");
            }
        }
    }

    private void showRoomOptionsDialog(Room room) {
        String currentStatus = room.getStatus();
        String toggleText = "Đã đặt".equalsIgnoreCase(currentStatus)
                ? "Chuyển trạng thái sang: TRỐNG (Khách trả phòng)"
                : "Chuyển trạng thái sang: ĐÃ ĐẶT (Có khách ở)";

        String[] options = {
                "Xem thông tin chi tiết phòng",
                toggleText,
                "Đặt phòng này cho khách",
                "Xóa phòng khỏi hệ thống"
        };

        new AlertDialog.Builder(this)
                .setTitle(room.getName() + " [" + room.getStatus() + "]")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRoomDetailDialog(room);
                    } else if (which == 1) {
                        databaseHelper.toggleRoomStatus(room.getId());
                        refreshRoomList();
                        Toast.makeText(this, "Đã cập nhật trạng thái phòng thành công! ✅", Toast.LENGTH_SHORT).show();
                    } else if (which == 2) {
                        Intent intent = new Intent(RoomListActivity.this, SimpleBookingActivity.class);
                        intent.putExtra("room_id", room.getId());
                        intent.putExtra("room_name", room.getName());
                        intent.putExtra("room_price", room.getPrice());
                        startActivity(intent);
                    } else if (which == 3) {
                        databaseHelper.deleteRoom(room.getId());
                        refreshRoomList();
                        Toast.makeText(this, "Đã xóa phòng khỏi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showRoomDetailDialog(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 20);

        ImageView img = new ImageView(this);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 420);
        imgLp.setMargins(0, 0, 0, 20);
        img.setLayoutParams(imgLp);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(this).load(room.getImageUrl()).into(img);
        } else {
            img.setImageResource(R.drawable.ic_hotel_logo);
        }
        layout.addView(img);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(room.getName());
        tvTitle.setTextSize(20);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.parseColor("#0F172A"));
        layout.addView(tvTitle);

        TextView tvStatus = new TextView(this);
        boolean isOccupied = "Đã đặt".equalsIgnoreCase(room.getStatus());
        tvStatus.setText("TRẠNG THÁI: " + (isOccupied ? "ĐÃ ĐẶT (CÓ KHÁCH Ở)" : "PHÒNG TRỐNG (SẮN SÀNG)"));
        tvStatus.setTextSize(14);
        tvStatus.setTypeface(null, Typeface.BOLD);
        tvStatus.setTextColor(isOccupied ? Color.parseColor("#DC2626") : Color.parseColor("#16A34A"));
        tvStatus.setPadding(0, 10, 0, 16);
        layout.addView(tvStatus);

        TextView tvPrice = new TextView(this);
        tvPrice.setText("Mức giá: " + String.format(Locale.getDefault(), "%,.0f VNĐ / đêm", room.getPrice()));
        tvPrice.setTextSize(16);
        tvPrice.setTypeface(null, Typeface.BOLD);
        tvPrice.setTextColor(Color.parseColor("#D97706"));
        layout.addView(tvPrice);

        TextView tvLocation = new TextView(this);
        tvLocation.setText("Địa điểm: " + (room.getLocation() != null ? room.getLocation() : "Hà Nội, Việt Nam"));
        tvLocation.setTextSize(14);
        tvLocation.setPadding(0, 8, 0, 8);
        layout.addView(tvLocation);

        TextView tvRating = new TextView(this);
        tvRating.setText("Đánh giá sao: " + room.getRating() + " ★");
        tvRating.setTextSize(14);
        tvRating.setPadding(0, 0, 0, 8);
        layout.addView(tvRating);

        TextView tvAmenities = new TextView(this);
        tvAmenities.setText("Tiện ích đi kèm:\n• " + (room.getAmenities() != null ? room.getAmenities() : "Chưa có"));
        tvAmenities.setTextSize(14);
        tvAmenities.setPadding(0, 8, 0, 8);
        layout.addView(tvAmenities);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Mô tả chi tiết phòng:\n" + (room.getDescription() != null ? room.getDescription() : "Phòng nghỉ hiện đại khép kín."));
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(Color.parseColor("#475569"));
        tvDesc.setPadding(0, 8, 0, 8);
        layout.addView(tvDesc);

        scrollView.addView(layout);
        builder.setView(scrollView);

        String toggleText = isOccupied ? "Đổi sang TRỐNG (Trả phòng)" : "Đổi sang ĐÃ ĐẶT (Nhận phòng)";
        builder.setPositiveButton(toggleText, (dialog, which) -> {
            databaseHelper.toggleRoomStatus(room.getId());
            refreshRoomList();
            Toast.makeText(this, "Đã cập nhật trạng thái phòng! ✅", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Đóng", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRoomList();
    }

    private void refreshRoomList() {
        if (databaseHelper != null) {
            List<Room> freshRooms = databaseHelper.getRoomsByStatus(filterStatus);
            roomList.clear();
            roomList.addAll(freshRooms);
            if (lvDanhSachPhong.getAdapter() != null) {
                ((RoomAdapter) lvDanhSachPhong.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    public void onAddRoomClick(View view) {
        Intent intent = new Intent(this, com.example.datphongks.ui.admin.AddRoomActivity.class);
        startActivity(intent);
    }
}
