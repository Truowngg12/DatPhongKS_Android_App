package com.example.datphongks.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datphongks.data.repository.SqliteUserRepository;
import com.example.datphongks.databinding.ActivityAdminDashboardBinding;
import com.example.datphongks.simplebooking.DatabaseHelper;
import com.example.datphongks.simplebooking.RoomListActivity;
import com.example.datphongks.ui.auth.AuthActivity;
import com.example.datphongks.utils.SessionManager;

import java.util.Locale;

/**
 * Màn hình vào cửa dành riêng cho Admin (role == 1).
 * - Bảng thống kê Real-time: Tổng số phòng, số lượt booking, phòng trống, phòng đã đặt, doanh thu.
 * - Nhấp vào "Phòng Trống" / "Phòng Đã Đặt" để lọc xem chi tiết các phòng tương ứng.
 * - Hỗ trợ đăng xuất quay lại trang đăng nhập lập tức.
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        if (!sessionManager.isAdmin()) {
            startActivity(new Intent(this, com.example.datphongks.MainActivity.class));
            finish();
            return;
        }

        binding.tvWelcomeAdmin.setText("Chào mừng Quản trị viên");

        // Nút Thêm phòng mới
        binding.btnAddRoom.setOnClickListener(v ->
                startActivity(new Intent(this, AddRoomActivity.class)));

        // Nút Quản lý tất cả danh sách phòng
        binding.btnManageRooms.setOnClickListener(v -> openRoomListFiltered("ALL"));
        binding.cardAllRooms.setOnClickListener(v -> openRoomListFiltered("ALL"));

        // Bấm vào thẻ "Phòng Trống" -> Lọc danh sách chỉ hiện phòng Trống
        binding.cardAvailable.setOnClickListener(v -> openRoomListFiltered("Trống"));

        // Bấm vào thẻ "Phòng Đã Đặt" -> Lọc danh sách chỉ hiện phòng Đã đặt
        binding.cardOccupied.setOnClickListener(v -> openRoomListFiltered("Đã đặt"));

        // Đăng xuất Admin (Nút trên Toolbar)
        binding.btnLogoutAdminTop.setOnClickListener(v -> performAdminLogout());

        // Đăng xuất Admin (Nút ở cuối trang)
        binding.btnLogoutAdminBottom.setOnClickListener(v -> performAdminLogout());
    }

    private void openRoomListFiltered(String statusFilter) {
        Intent intent = new Intent(this, RoomListActivity.class);
        intent.putExtra("filter_status", statusFilter);
        startActivity(intent);
    }

    private void performAdminLogout() {
        new SqliteUserRepository(this).logout();
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRealTimeStatistics();
    }

    private void loadRealTimeStatistics() {
        if (dbHelper != null) {
            int totalRooms = dbHelper.getRoomCount();
            int totalBookings = dbHelper.getBookingCount();
            int occupiedRooms = dbHelper.getOccupiedRoomCount();
            int availableRooms = dbHelper.getAvailableRoomCount();
            double totalRevenue = dbHelper.getTotalRevenue();

            binding.tvStatRooms.setText(String.valueOf(totalRooms));
            binding.tvStatBookings.setText(String.valueOf(totalBookings));
            binding.tvStatOccupied.setText(String.valueOf(occupiedRooms));
            binding.tvStatAvailable.setText(String.valueOf(availableRooms));
            binding.tvStatRevenue.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalRevenue));
        }
    }
}
