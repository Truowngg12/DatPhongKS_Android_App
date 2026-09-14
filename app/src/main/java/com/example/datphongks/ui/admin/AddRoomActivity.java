package com.example.datphongks.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.datphongks.R;
import com.example.datphongks.simplebooking.DatabaseHelper;
import com.google.android.material.card.MaterialCardView;

public class AddRoomActivity extends AppCompatActivity {

    private EditText edtRoomName, edtRoomPrice, edtRoomAmenities;
    private EditText edtRoomLocation, edtRoomDescription, edtRoomRating;
    private MaterialCardView cardSelectImage;
    private ImageView imgRoomPreview;
    private LinearLayout layoutUploadPlaceholder;
    private Button btnSaveRoom;
    private DatabaseHelper databaseHelper;

    private String selectedImageUriString = "";

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        try {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {}

                        selectedImageUriString = uri.toString();

                        Glide.with(this)
                                .load(uri)
                                .into(imgRoomPreview);

                        layoutUploadPlaceholder.setVisibility(View.GONE);
                        imgRoomPreview.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Đã chọn ảnh thành công!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi nạp hình ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        databaseHelper = new DatabaseHelper(this);

        edtRoomName = findViewById(R.id.edtRoomName);
        edtRoomPrice = findViewById(R.id.edtRoomPrice);
        edtRoomLocation = findViewById(R.id.edtRoomLocation);
        edtRoomDescription = findViewById(R.id.edtRoomDescription);
        edtRoomRating = findViewById(R.id.edtRoomRating);
        edtRoomAmenities = findViewById(R.id.edtRoomAmenities);

        cardSelectImage = findViewById(R.id.cardSelectImage);
        imgRoomPreview = findViewById(R.id.imgRoomPreview);
        layoutUploadPlaceholder = findViewById(R.id.layoutUploadPlaceholder);
        btnSaveRoom = findViewById(R.id.btnSaveRoom);

        cardSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSaveRoom.setOnClickListener(v -> {
            String name = edtRoomName.getText().toString().trim();
            String priceStr = edtRoomPrice.getText().toString().trim();
            String location = edtRoomLocation.getText().toString().trim();
            String description = edtRoomDescription.getText().toString().trim();
            String ratingStr = edtRoomRating.getText().toString().trim();
            String amenities = edtRoomAmenities.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Tên phòng và Giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            double rating = 4.5;
            if (!ratingStr.isEmpty()) {
                try {
                    rating = Double.parseDouble(ratingStr);
                } catch (NumberFormatException ignored) {}
            }

            if (location.isEmpty()) location = "Hà Nội, Việt Nam";
            if (description.isEmpty()) description = "Phòng nghỉ hiện đại, không gian thoải mái và đầy đủ tiện nghi.";

            String imageUrl = selectedImageUriString;
            if (imageUrl.isEmpty()) {
                imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80";
            }

            long insertedId = databaseHelper.addRoom(
                    name,
                    (int) price,
                    R.drawable.ic_hotel_logo,
                    imageUrl,
                    amenities,
                    "Trống",
                    location,
                    description,
                    rating
            );

            if (insertedId != -1) {
                Toast.makeText(this, "Thêm phòng mới thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Thêm phòng thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
