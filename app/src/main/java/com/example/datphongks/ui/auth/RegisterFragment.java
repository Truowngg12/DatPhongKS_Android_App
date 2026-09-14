package com.example.datphongks.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.datphongks.MainActivity;
import com.example.datphongks.databinding.FragmentRegisterBinding;
import com.example.datphongks.viewmodels.AuthViewModel;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeViewModel();

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            // MỚI (Phần 1): lấy thêm số điện thoại từ ô etPhone (R.id.etPhone)
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            viewModel.register(name, email, phone, password, confirmPassword);
        });

        binding.tvGoToLogin.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getNameError().observe(getViewLifecycleOwner(), error -> binding.tilName.setError(error));
        viewModel.getEmailError().observe(getViewLifecycleOwner(), error -> binding.tilEmail.setError(error));
        // MỚI (Phần 1): hiển thị lỗi validate số điện thoại lên ô tilPhone
        viewModel.getPhoneError().observe(getViewLifecycleOwner(), error -> binding.tilPhone.setError(error));
        viewModel.getPasswordError().observe(getViewLifecycleOwner(), error -> binding.tilPassword.setError(error));
        viewModel.getConfirmPasswordError().observe(getViewLifecycleOwner(), error -> binding.tilConfirmPassword.setError(error));

        // Email đã tồn tại / lỗi khác -> Toast
        viewModel.getGeneralError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Đăng ký thành công -> tài khoản mới LUÔN là User thường (role = 0)
        // nên sau khi đăng ký chỉ cần vào thẳng MainActivity (HomeFragment)
        viewModel.getRegisteredUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null || getActivity() == null) return;
            Toast.makeText(getContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
