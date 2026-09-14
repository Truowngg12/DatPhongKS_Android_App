package com.example.datphongks.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.datphongks.data.models.User;
import com.example.datphongks.data.repository.SqliteUserRepository;
import com.example.datphongks.databinding.FragmentProfileBinding;
import com.example.datphongks.ui.admin.AdminDashboardActivity;
import com.example.datphongks.ui.auth.AuthActivity;
import com.example.datphongks.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    // ĐÃ SỬA (Phần 1): dùng SqliteUserRepository thật (đọc dữ liệu SQLite theo
    // session đang đăng nhập) thay vì DummyUserRepository (dữ liệu giả cứng "u1").
    private SqliteUserRepository repository;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new SqliteUserRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        loadUserProfile();

        // Sự kiện đăng xuất
        binding.btnLogout.setOnClickListener(v -> {
            repository.logout();
            startActivity(new Intent(getActivity(), AuthActivity.class));
            getActivity().finishAffinity();
        });

        // ĐÃ SỬA LỖI (Phần 1): trước đây gọi tới
        // "com.example.datphongks.simplebooking.AdminActivity" - class này KHÔNG TỒN TẠI
        // -> làm project không biên dịch được. Đã trỏ đúng về AdminDashboardActivity.
        binding.btnAdminManager.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AdminDashboardActivity.class)));

        // YÊU CẦU PHÂN QUYỀN: "User tuyệt đối không thấy nút hay tính năng của Admin"
        // -> Ẩn hẳn nút quản trị nếu người đang đăng nhập không phải Admin (role != 1)
        binding.btnAdminManager.setVisibility(sessionManager.isAdmin() ? View.VISIBLE : View.GONE);

        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    private void showEditProfileDialog() {
        User user = repository.getCurrentUser();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa thông tin cá nhân");

        android.widget.ScrollView scrollView = new android.widget.ScrollView(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        final com.google.android.material.textfield.TextInputLayout tilName = createTextInputLayout("Họ tên");
        final EditText etName = tilName.getEditText();
        if (etName != null) etName.setText(user.getFullName());
        layout.addView(tilName);

        final com.google.android.material.textfield.TextInputLayout tilPhone = createTextInputLayout("Số điện thoại");
        final EditText etPhone = tilPhone.getEditText();
        if (etPhone != null) {
            etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
            etPhone.setText(user.getPhone());
        }
        layout.addView(tilPhone);

        final com.google.android.material.textfield.TextInputLayout tilAddress = createTextInputLayout("Địa chỉ");
        final EditText etAddress = tilAddress.getEditText();
        if (etAddress != null) etAddress.setText(user.getAddress());
        layout.addView(tilAddress);

        final com.google.android.material.textfield.TextInputLayout tilIdCard = createTextInputLayout("Số CMND / CCCD");
        final EditText etIdCard = tilIdCard.getEditText();
        if (etIdCard != null) {
            etIdCard.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            etIdCard.setText(user.getIdCard());
        }
        layout.addView(tilIdCard);

        final com.google.android.material.textfield.TextInputLayout tilRelPhone = createTextInputLayout("Số điện thoại người thân");
        final EditText etRelativePhone = tilRelPhone.getEditText();
        if (etRelativePhone != null) {
            etRelativePhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
            etRelativePhone.setText(user.getRelativePhone());
        }
        layout.addView(tilRelPhone);

        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Lưu thay đổi", (dialog, which) -> {
            String newName = etName != null ? etName.getText().toString().trim() : "";
            String newPhone = etPhone != null ? etPhone.getText().toString().trim() : "";
            String newAddress = etAddress != null ? etAddress.getText().toString().trim() : "";
            String newIdCard = etIdCard != null ? etIdCard.getText().toString().trim() : "";
            String newRelPhone = etRelativePhone != null ? etRelativePhone.getText().toString().trim() : "";

            if (newName.isEmpty()) {
                Toast.makeText(getContext(), "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi repository cập nhật DB
            boolean success = repository.updateProfile(
                    user.getId(),
                    newName,
                    user.getEmail(),
                    newPhone,
                    newAddress,
                    newIdCard,
                    newRelPhone
            );
            
            if (success) {
                Toast.makeText(getContext(), "Cập nhật thông tin thành công! ✅", Toast.LENGTH_SHORT).show();
                loadUserProfile(); // Cập nhật lại UI
            } else {
                Toast.makeText(getContext(), "Lỗi khi cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private com.google.android.material.textfield.TextInputLayout createTextInputLayout(String hint) {
        com.google.android.material.textfield.TextInputLayout til = new com.google.android.material.textfield.TextInputLayout(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 12, 0, 12);
        til.setLayoutParams(lp);
        til.setHint(hint);

        com.google.android.material.textfield.TextInputEditText et = new com.google.android.material.textfield.TextInputEditText(til.getContext());
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        til.addView(et);
        return til;
    }

    private void loadUserProfile() {
        User user = repository.getCurrentUser();
        binding.tvUserName.setText(user.getFullName());
        binding.tvUserEmail.setText(user.getEmail());

        Glide.with(this)
                .load(user.getAvatarUrl())
                .into(binding.ivAvatar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
