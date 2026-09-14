package com.example.datphongks.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datphongks.data.models.User;
import com.example.datphongks.data.repository.SqliteUserRepository;
import com.example.datphongks.utils.SessionManager;

/**
 * ĐÃ NÂNG CẤP (Phần 1):
 *  - Đổi từ ViewModel -> AndroidViewModel để có Context, dùng để mở SQLite.
 *  - login()/register() giờ thao tác THẬT với bảng User trong SQLite
 *    (thay vì chỉ validate định dạng rồi coi như thành công).
 *  - Sau khi login/register thành công, LƯU SESSION (SessionManager) và
 *    bắn ra loggedInUser để Fragment biết role mà điều hướng đúng màn hình
 *    (role == 1 -> AdminDashboardActivity, role == 0 -> HomeFragment).
 */
public class AuthViewModel extends AndroidViewModel {

    private final SqliteUserRepository userRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<String> emailError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    private final MutableLiveData<String> nameError = new MutableLiveData<>();
    private final MutableLiveData<String> phoneError = new MutableLiveData<>();
    private final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();

    // Kết quả login/register trả về nguyên đối tượng User (có role) để Fragment điều hướng
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();
    private final MutableLiveData<User> registeredUser = new MutableLiveData<>();

    // Thông báo lỗi chung (sai tài khoản/mật khẩu, email đã tồn tại...)
    private final MutableLiveData<String> generalError = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new SqliteUserRepository(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<String> getEmailError() { return emailError; }
    public LiveData<String> getPasswordError() { return passwordError; }
    public LiveData<String> getNameError() { return nameError; }
    public LiveData<String> getPhoneError() { return phoneError; }
    public LiveData<String> getConfirmPasswordError() { return confirmPasswordError; }
    public LiveData<User> getLoggedInUser() { return loggedInUser; }
    public LiveData<User> getRegisteredUser() { return registeredUser; }
    public LiveData<String> getGeneralError() { return generalError; }

    public void login(String email, String password) {
        boolean isValid = true;

        String cleanEmail = email != null ? email.trim() : "";
        String cleanPassword = password != null ? password.trim() : "";

        if (cleanEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            emailError.setValue("Email không hợp lệ");
            isValid = false;
        } else {
            emailError.setValue(null);
        }

        if (cleanPassword.length() < 6) {
            passwordError.setValue("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            passwordError.setValue(null);
        }

        if (!isValid) {
            return;
        }

        // Kiểm tra thật trong SQLite
        User user = userRepository.login(cleanEmail, cleanPassword);
        if (user == null) {
            generalError.setValue("Sai email hoặc mật khẩu");
            return;
        }

        // Lưu phiên đăng nhập (userId + role) để các màn hình khác dùng lại
        sessionManager.login(user);
        loggedInUser.setValue(user);
    }

    public void register(String name, String email, String phone, String password, String confirmPassword) {
        boolean isValid = true;

        if (name == null || name.isEmpty()) {
            nameError.setValue("Vui lòng nhập họ tên");
            isValid = false;
        } else {
            nameError.setValue(null);
        }

        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setValue("Email không hợp lệ");
            isValid = false;
        } else {
            emailError.setValue(null);
        }

        if (phone == null || !android.util.Patterns.PHONE.matcher(phone).matches() || phone.length() < 9) {
            phoneError.setValue("Số điện thoại không hợp lệ");
            isValid = false;
        } else {
            phoneError.setValue(null);
        }

        if (password == null || password.length() < 6) {
            passwordError.setValue("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            passwordError.setValue(null);
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            confirmPasswordError.setValue("Mật khẩu xác nhận không khớp");
            isValid = false;
        } else {
            confirmPasswordError.setValue(null);
        }

        if (!isValid) {
            return;
        }

        if (userRepository.isEmailTaken(email)) {
            generalError.setValue("Email này đã được đăng ký");
            return;
        }

        // Tạo tài khoản thật trong SQLite (role luôn = 0, User thường)
        User newUser = userRepository.register(name, email, password, phone);
        if (newUser == null) {
            generalError.setValue("Đăng ký thất bại, vui lòng thử lại");
            return;
        }

        // Tự động đăng nhập luôn sau khi đăng ký thành công
        sessionManager.login(newUser);
        registeredUser.setValue(newUser);
    }
}
