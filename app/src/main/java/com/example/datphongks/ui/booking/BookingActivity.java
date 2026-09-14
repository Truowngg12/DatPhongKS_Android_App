package com.example.datphongks.ui.booking;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.example.datphongks.R;
import com.example.datphongks.data.models.User;
import com.example.datphongks.data.repository.SqliteUserRepository;
import com.example.datphongks.databinding.ActivityBookingBinding;
import com.example.datphongks.simplebooking.Booking;
import com.example.datphongks.simplebooking.DatabaseHelper;
import com.example.datphongks.simplebooking.Room;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TRANG ĐẶT PHÒNG & THANH TOÁN TẬP TRUNG (GOM THÀNH 1 TRANG DUY NHẤT)
 * - Nhập đầy đủ thông tin: Họ tên, SĐT, Số CMND/CCCD, Tên & SĐT người thân.
 * - Chọn thời gian lưu trú bằng MaterialDatePicker.
 * - Bảng tính giá cọc 30% nổi bật (Tiền phòng 100%, Tiền cọc 30%, Còn lại 70%).
 * - Đa dạng phương thức thanh toán có Icon chuyên nghiệp (MB Bank, MoMo, ZaloPay, Visa, Tiền mặt).
 */
public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private DatabaseHelper dbHelper;
    private SqliteUserRepository userRepository;
    private Room selectedRoom;

    private long checkInTimestamp = 0, checkOutTimestamp = 0;
    private int rentDays = 1;
    private double totalRoomPrice = 0;
    private double depositAmount = 0;
    private double remainingAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        userRepository = new SqliteUserRepository(this);

        // Nhận ID phòng từ Intent
        int roomId = getIntent().getIntExtra("room_id", -1);
        if (roomId == -1) {
            String hotelIdStr = getIntent().getStringExtra("hotel_id");
            if (hotelIdStr != null) {
                try {
                    roomId = Integer.parseInt(hotelIdStr);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (roomId != -1) {
            selectedRoom = dbHelper.getRoomById(roomId);
        }

        setupToolbar();
        setupRoomSummaryUI();
        prefillUserInfo();
        setupListeners();
        setupPaymentMethodsToggle();
        setupCopyButtons();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đặt phòng & Thanh toán Cọc");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRoomSummaryUI() {
        if (selectedRoom != null) {
            binding.tvRoomNameSummary.setText(selectedRoom.getName());
            binding.tvRoomPriceSummary.setText(String.format(Locale.getDefault(), "Giá: %,.0f VNĐ / đêm", selectedRoom.getPrice()));
            binding.tvRoomLocationSummary.setText(selectedRoom.getLocation() != null ? selectedRoom.getLocation() : "Hà Nội, Việt Nam");

            if (selectedRoom.getImageUrl() != null && !selectedRoom.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(selectedRoom.getImageUrl())
                        .into(binding.imgRoomSummary);
            } else {
                binding.imgRoomSummary.setImageResource(R.drawable.ic_hotel_logo);
            }

            totalRoomPrice = selectedRoom.getPrice();
            calculateDepositPrices();
        }
    }

    private void prefillUserInfo() {
        User currentUser = userRepository.getCurrentUser();
        if (currentUser != null && !"guest".equals(currentUser.getId())) {
            binding.etFullName.setText(currentUser.getFullName());
            binding.etPhone.setText(currentUser.getPhone());
            binding.etIdCard.setText(currentUser.getIdCard());
            binding.etRelativePhone.setText(currentUser.getRelativePhone());
        }
    }

    private void setupListeners() {
        binding.etCheckIn.setOnClickListener(v -> openRangeDatePicker());
        binding.etCheckOut.setOnClickListener(v -> openRangeDatePicker());

        binding.btnConfirmAndPay.setOnClickListener(v -> processBookingAndPayment());
    }

    private void openRangeDatePicker() {
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Chọn ngày nhận - trả phòng")
                .setSelection(new Pair<>(MaterialDatePicker.todayInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds() + 86400000))
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            checkInTimestamp = selection.first;
            checkOutTimestamp = selection.second;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.etCheckIn.setText(sdf.format(new Date(checkInTimestamp)));
            binding.etCheckOut.setText(sdf.format(new Date(checkOutTimestamp)));

            long diff = checkOutTimestamp - checkInTimestamp;
            rentDays = (int) (diff / (24 * 60 * 60 * 1000));
            if (rentDays < 1) rentDays = 1;

            binding.tvRentDaysCount.setText("Tổng số đêm lưu trú: " + rentDays + " đêm");
            calculateDepositPrices();
        });
        picker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
    }

    private void calculateDepositPrices() {
        if (selectedRoom != null) {
            totalRoomPrice = rentDays * selectedRoom.getPrice();
        }
        depositAmount = totalRoomPrice * 0.30; // Cọc 30%
        remainingAmount = totalRoomPrice - depositAmount; // Còn lại 70%

        binding.tvTotalRoomPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalRoomPrice));
        binding.tvDepositAmount.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", depositAmount));
        binding.tvRemainingAmount.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", remainingAmount));
        binding.tvBottomDeposit.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", depositAmount));
    }

    private void setupPaymentMethodsToggle() {
        binding.rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            binding.layoutBankDetails.setVisibility(View.GONE);
            binding.layoutMomoDetails.setVisibility(View.GONE);
            binding.layoutZalopayDetails.setVisibility(View.GONE);
            binding.layoutCreditCardDetails.setVisibility(View.GONE);
            binding.layoutCashDetails.setVisibility(View.GONE);

            if (checkedId == R.id.rbBankTransfer) {
                binding.layoutBankDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbMomo) {
                binding.layoutMomoDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbZalopay) {
                binding.layoutZalopayDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbCreditCard) {
                binding.layoutCreditCardDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbCash) {
                binding.layoutCashDetails.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupCopyButtons() {
        binding.btnCopyBankAcc.setOnClickListener(v -> {
            copyToClipboard("Số tài khoản MB Bank", "9999888888");
            Toast.makeText(this, "Đã sao chép STK MB Bank: 9999888888 📋", Toast.LENGTH_SHORT).show();
        });

        binding.btnCopyMomo.setOnClickListener(v -> {
            copyToClipboard("Số MoMo", "0900000000");
            Toast.makeText(this, "Đã sao chép SĐT MoMo: 0900000000 📋", Toast.LENGTH_SHORT).show();
        });

        binding.btnCopyZalopay.setOnClickListener(v -> {
            copyToClipboard("Số ZaloPay", "0900000000");
            Toast.makeText(this, "Đã sao chép SĐT ZaloPay: 0900000000 📋", Toast.LENGTH_SHORT).show();
        });
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void processBookingAndPayment() {
        String name = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String idCard = binding.etIdCard.getText().toString().trim();
        String relativeName = binding.etRelativeName.getText().toString().trim();
        String relativePhone = binding.etRelativePhone.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Họ và tên khách hàng", Toast.LENGTH_SHORT).show();
            binding.tilFullName.setError("Bắt buộc nhập");
            return;
        } else {
            binding.tilFullName.setError(null);
        }

        if (phone.length() < 9) {
            Toast.makeText(this, "Vui lòng nhập Số điện thoại hợp lệ", Toast.LENGTH_SHORT).show();
            binding.tilPhone.setError("Bắt buộc nhập (tối thiểu 9 số)");
            return;
        } else {
            binding.tilPhone.setError(null);
        }

        if (idCard.length() < 9) {
            Toast.makeText(this, "Vui lòng nhập Số CMND / CCCD hợp lệ", Toast.LENGTH_SHORT).show();
            binding.tilIdCard.setError("Bắt buộc nhập (9-12 số)");
            return;
        } else {
            binding.tilIdCard.setError(null);
        }

        if (checkInTimestamp == 0) {
            Toast.makeText(this, "Vui lòng chọn ngày nhận / trả phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedMethodId = binding.rgPaymentMethods.getCheckedRadioButtonId();
        if (selectedMethodId == R.id.rbCreditCard) {
            String cardNumber = binding.etCardNumber.getText().toString().trim();
            String cardExpiry = binding.etCardExpiry.getText().toString().trim();
            String cardCvv = binding.etCardCvv.getText().toString().trim();

            if (cardNumber.length() < 12) {
                Toast.makeText(this, "Vui lòng nhập Số thẻ tín dụng hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardExpiry.length() < 4) {
                Toast.makeText(this, "Vui lòng nhập Ngày hết hạn (MM/YY)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardCvv.length() < 3) {
                Toast.makeText(this, "Vui lòng nhập Mã CVV hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (selectedRoom == null) {
            Toast.makeText(this, "Dữ liệu phòng không hợp lệ, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu đơn hàng đặt cọc vào SQLite
        Booking newBooking = new Booking(selectedRoom.getId(), name, phone, rentDays, totalRoomPrice);
        long result = dbHelper.insertBooking(newBooking);

        if (result != -1) {
            Toast.makeText(this, "Đặt cọc phòng (30%) thành công! 🎉", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, ConfirmationActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            Toast.makeText(this, "Lỗi khi lưu thông tin đặt phòng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }
}
