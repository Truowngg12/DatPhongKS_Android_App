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
import com.example.datphongks.databinding.FragmentLoginBinding;
import com.example.datphongks.ui.admin.AdminDashboardActivity;
import com.example.datphongks.viewmodels.AuthViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeViewModel();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            viewModel.login(email, password);
        });

        binding.btnGoogle.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đăng nhập bằng Google đang phát triển", Toast.LENGTH_SHORT).show();
        });

        binding.btnFacebook.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đăng nhập bằng Facebook đang phát triển", Toast.LENGTH_SHORT).show();
        });

        binding.tvGoToRegister.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).replaceFragment(new RegisterFragment(), true);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getEmailError().observe(getViewLifecycleOwner(), error -> binding.tilEmail.setError(error));
        viewModel.getPasswordError().observe(getViewLifecycleOwner(), error -> binding.tilPassword.setError(error));

        // Sai email/mật khẩu -> hiện Toast báo lỗi
        viewModel.getGeneralError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // ĐÃ CẬP NHẬT (Phần 1): Login thành công -> điều hướng theo role
        // role == 1 (Admin) -> AdminDashboardActivity
        // role == 0 (User)  -> MainActivity (mặc định hiển thị HomeFragment)
        viewModel.getLoggedInUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null || getActivity() == null) return;

            if (user.isAdmin()) {
                Toast.makeText(getContext(), "Chào Admin " + user.getFullName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), AdminDashboardActivity.class));
            } else {
                Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
            getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
