package com.example.datphongks.ui.booking;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datphongks.R;
import com.example.datphongks.databinding.ActivityPaymentBinding;
import com.example.datphongks.simplebooking.Booking;
import com.example.datphongks.simplebooking.DatabaseHelper;

import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private DatabaseHelper dbHelper;

    private int roomId = -1;
    private String customerName = "";
    private String customerPhone = "";
    private int rentDays = 1;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupToolbar();
        receiveIntentData();
        setupPaymentMethods();
        setupCopyButtons();

        binding.btnPayNow.setOnClickListener(v -> processPaymentAndFinish());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán & Xác nhận");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void receiveIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            roomId = intent.getIntExtra("room_id", -1);
            customerName = intent.getStringExtra("customer_name");
            if (customerName == null) customerName = intent.getStringExtra("ten_khach");
            customerPhone = intent.getStringExtra("customer_phone");
            if (customerPhone == null) customerPhone = intent.getStringExtra("so_dien_thoai");
            rentDays = intent.getIntExtra("rent_days", 1);
            if (rentDays <= 0) rentDays = intent.getIntExtra("so_ngay", 1);
            totalPrice = intent.getDoubleExtra("total_price", 0);
            if (totalPrice <= 0) totalPrice = intent.getDoubleExtra("tong_tien", 0);
        }

        if (customerName == null) customerName = "Khách hàng";
        if (customerPhone == null) customerPhone = "";

        binding.tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalPrice));
        binding.tvTransferContent.setText("Cú pháp CK: DATPHONG " + (customerPhone.isEmpty() ? "KHACHHANG" : customerPhone));
    }

    private void setupPaymentMethods() {
        binding.rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            binding.cardBankDetails.setVisibility(View.GONE);
            binding.cardMomoDetails.setVisibility(View.GONE);
            binding.cardCreditCardDetails.setVisibility(View.GONE);
            binding.cardCashDetails.setVisibility(View.GONE);

            if (checkedId == R.id.rbBankTransfer) {
                binding.cardBankDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbMomo) {
                binding.cardMomoDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbCreditCard) {
                binding.cardCreditCardDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbCash) {
                binding.cardCashDetails.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupCopyButtons() {
        binding.btnCopyBankAcc.setOnClickListener(v -> {
            copyToClipboard("Số tài khoản MB Bank", "9999888888");
            Toast.makeText(this, "Đã sao chép số tài khoản: 9999888888 📋", Toast.LENGTH_SHORT).show();
        });

        binding.btnCopyMomo.setOnClickListener(v -> {
            copyToClipboard("Số MoMo", "0900000000");
            Toast.makeText(this, "Đã sao chép SĐT MoMo: 0900000000 📋", Toast.LENGTH_SHORT).show();
        });
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void processPaymentAndFinish() {
        int selectedMethodId = binding.rgPaymentMethods.getCheckedRadioButtonId();

        // Nếu chọn thẻ tín dụng, kiểm tra nhập thẻ
        if (selectedMethodId == R.id.rbCreditCard) {
            String cardNumber = binding.etCardNumber.getText().toString().trim();
            String cardExpiry = binding.etCardExpiry.getText().toString().trim();
            String cardCvv = binding.etCardCvv.getText().toString().trim();

            if (cardNumber.length() < 12) {
                Toast.makeText(this, "Vui lòng nhập số thẻ tín dụng hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardExpiry.length() < 4) {
                Toast.makeText(this, "Vui lòng nhập ngày hết hạn (MM/YY)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardCvv.length() < 3) {
                Toast.makeText(this, "Vui lòng nhập mã CVV/CVC hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Lưu đơn hàng vào SQLite
        if (roomId != -1) {
            Booking booking = new Booking(roomId, customerName, customerPhone, rentDays, totalPrice);
            dbHelper.insertBooking(booking);
        }

        Toast.makeText(this, "Đặt phòng thành công! 🎉", Toast.LENGTH_LONG).show();

        // Chuyển sang màn hình xác nhận
        Intent intent = new Intent(this, ConfirmationActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
