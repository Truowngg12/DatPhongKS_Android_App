package com.example.datphongks.simplebooking;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datphongks.R;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView lvAdminRooms;
    private List<Room> roomList;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        databaseHelper = new DatabaseHelper(this);
        lvAdminRooms = findViewById(R.id.lvDanhSachPhong);

        if (databaseHelper.getAllRooms().isEmpty()) {
            databaseHelper.addRoom("Phòng Standard", 150000, R.drawable.ic_hotel_logo, "", "WiFi, Máy lạnh", "Trống");
            databaseHelper.addRoom("Phòng Deluxe", 250000, R.drawable.ic_hotel_logo, "", "Bồn tắm, View biển", "Trống");
            databaseHelper.addRoom("Phòng VIP", 400000, R.drawable.ic_hotel_logo, "", "Hồ bơi riêng", "Trống");
        }

        loadRoomData();

        lvAdminRooms.setOnItemClickListener((parent, view, position, id) -> {
            // Sử dụng parent.getItemAtPosition để lấy đúng đối tượng dữ liệu gắn với dòng đó
            Object item = parent.getItemAtPosition(position);
            if (!(item instanceof Room)) return;
            
            Room selectedRoom = (Room) item;

            CharSequence[] options = {"Xóa phòng này", "Đổi trạng thái thành 'Đã đặt'", "Hủy"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quản lý: " + selectedRoom.getName());
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    databaseHelper.deleteRoom(selectedRoom.getId());
                    Toast.makeText(this, "Đã xóa phòng!", Toast.LENGTH_SHORT).show();
                    loadRoomData();
                } else if (which == 1) {
                    databaseHelper.updateRoomStatus(selectedRoom.getId(), "Đã đặt");
                    Toast.makeText(this, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                    loadRoomData();
                }
            });
            builder.show();
        });
    }

    public void onAddRoomClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Phòng Mới");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputName = new EditText(this);
        inputName.setHint("Tên phòng (VD: Phòng Tổng thống)");
        layout.addView(inputName);

        final EditText inputPrice = new EditText(this);
        inputPrice.setHint("Giá tiền (VD: 5000000)");
        inputPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputPrice);

        final EditText inputAmenities = new EditText(this);
        inputAmenities.setHint("Tiện ích (VD: Ăn sáng, Hồ bơi)");
        layout.addView(inputAmenities);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String priceStr = inputPrice.getText().toString().trim();
            String amenities = inputAmenities.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Lỗi: Hãy nhập đủ Tên và Giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            long insertedId = databaseHelper.addRoom(name, (int) price, R.drawable.ic_hotel_logo, "", amenities, "Trống");

            if (insertedId != -1) {
                // Yêu cầu 3: Thông báo Toast và đóng màn hình/dialog thêm phòng
                Toast.makeText(this, "Đã thêm phòng " + name + " thành công!", Toast.LENGTH_SHORT).show();
                
                // Cập nhật ngay danh sách ở AdminActivity
                loadRoomData();

                // Đóng Dialog
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Thêm phòng thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void loadRoomData() {
        List<Room> freshRooms = databaseHelper.getAllRooms();
        if (roomList == null) {
            roomList = freshRooms;
            roomAdapter = new RoomAdapter(this, roomList);
            lvAdminRooms.setAdapter(roomAdapter);
        } else {
            roomList.clear();
            roomList.addAll(freshRooms);
            roomAdapter.notifyDataSetChanged();
        }
        
        // Đảm bảo ListView cuộn về cuối để thấy phòng vừa thêm (nếu cần)
        if (!freshRooms.isEmpty()) {
            lvAdminRooms.setSelection(freshRooms.size() - 1);
        }
    }
}