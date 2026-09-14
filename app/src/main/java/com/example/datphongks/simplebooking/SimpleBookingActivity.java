package com.example.datphongks.simplebooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datphongks.R;

/**
 * Màn hình đặt phòng (đây chính là "BookingActivity" theo yêu cầu ban đầu,
 * nhưng đổi tên thành SimpleBookingActivity để KHÔNG đè lên
 * ui/booking/BookingActivity.java gốc của project - vốn đang xử lý chọn ngày
 * check-in/check-out cho luồng Hotel/Payment hiện có).
 *
 * QUY ƯỚC ID GIAO DIỆN (người vẽ layout tạo file res/layout/activity_simple_booking.xml):
 *   - R.id.edtTenKhach      : EditText -> tên khách hàng
 *   - R.id.edtSoDienThoai   : EditText -> số điện thoại
 *   - R.id.edtSoNgay        : EditText -> số ngày thuê
 *   - R.id.btnXacNhan       : Button   -> nút xác nhận đặt phòng
 */
public class SimpleBookingActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private EditText edtTenKhach;
    private EditText edtSoDienThoai;
    private EditText edtSoNgay;
    private Button btnXacNhan;

    // Dữ liệu phòng nhận được từ RoomListActivity thông qua Intent
    private int roomId;
    private String roomName;
    private double roomPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // LƯU Ý: người vẽ giao diện tạo file res/layout/activity_simple_booking.xml
        setContentView(R.layout.activity_simple_booking);

        // Bước 1: Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Bước 2: Nhận dữ liệu phòng được truyền từ Intent (từ RoomListActivity)
        roomId = getIntent().getIntExtra("room_id", -1);
        roomName = getIntent().getStringExtra("room_name");
        roomPrice = getIntent().getDoubleExtra("room_price", 0);

        // Bước 3: Ánh xạ các View từ layout
        edtTenKhach = findViewById(R.id.edtTenKhach);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtSoNgay = findViewById(R.id.edtSoNgay);
        btnXacNhan = findViewById(R.id.btnXacNhan);

        // Bước 4: Bắt sự kiện nút Xác nhận
        btnXacNhan.setOnClickListener(v -> xuLyXacNhanDatPhong());
    }

    private void xuLyXacNhanDatPhong() {
        String tenKhach = edtTenKhach.getText().toString().trim();
        String soDienThoai = edtSoDienThoai.getText().toString().trim();
        String soNgayStr = edtSoNgay.getText().toString().trim();

        // Kiểm tra dữ liệu nhập vào cơ bản
        if (TextUtils.isEmpty(tenKhach) || TextUtils.isEmpty(soDienThoai) || TextUtils.isEmpty(soNgayStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int soNgay;
        try {
            soNgay = Integer.parseInt(soNgayStr);
            if (soNgay <= 0) {
                Toast.makeText(this, "Số ngày thuê phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số ngày thuê không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tính tổng tiền = giá phòng * số ngày thuê
        double tongTien = roomPrice * soNgay;

        // Chuyển sang trang Đặt phòng & Thanh toán Cọc tập trung
        android.content.Intent intent = new android.content.Intent(this, com.example.datphongks.ui.booking.BookingActivity.class);
        intent.putExtra("room_id", roomId);
        intent.putExtra("room_name", roomName);
        intent.putExtra("room_price", roomPrice);
        startActivity(intent);
        finish();
    }
}
