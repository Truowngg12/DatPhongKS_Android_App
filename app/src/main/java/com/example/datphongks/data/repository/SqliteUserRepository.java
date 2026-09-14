package com.example.datphongks.data.repository;

import android.content.Context;

import com.example.datphongks.data.local.AppDatabaseHelper;
import com.example.datphongks.data.models.User;
import com.example.datphongks.utils.SessionManager;

/**
 * Repository User "thật", đọc/ghi từ SQLite (AppDatabaseHelper) thay vì
 * dữ liệu giả như DummyUserRepository trước đây.
 *
 * Vẫn implements UserRepository (interface cũ) để không phá bất kỳ chỗ nào
 * khác trong code đang phụ thuộc vào interface đó. Ngoài ra bổ sung thêm
 * các hàm login()/register()/updateProfile()/isEmailTaken() phục vụ Phần 1.
 */
public class SqliteUserRepository implements UserRepository {

    private final AppDatabaseHelper dbHelper;
    private final SessionManager sessionManager;

    public SqliteUserRepository(Context context) {
        this.dbHelper = new AppDatabaseHelper(context);
        this.sessionManager = new SessionManager(context);
    }

    /**
     * Đăng nhập: kiểm tra email/mật khẩu trong SQLite.
     * @return User nếu đúng, null nếu sai.
     */
    public User login(String email, String password) {
        return dbHelper.checkLogin(email, password);
    }

    /**
     * Đăng ký tài khoản User mới (role mặc định = 0).
     * @return User vừa tạo, hoặc null nếu email đã tồn tại.
     */
    public User register(String fullName, String email, String password, String phone) {
        long newId = dbHelper.registerUser(fullName, email, password, phone);
        if (newId == -1) {
            return null; // email đã tồn tại hoặc lỗi insert
        }
        return dbHelper.getUserById(String.valueOf(newId));
    }

    public boolean isEmailTaken(String email) {
        return dbHelper.isEmailExists(email);
    }

    public boolean updateProfile(String userId, String fullName, String email, String phone) {
        return dbHelper.updateProfile(userId, fullName, email, phone);
    }

    public boolean updateProfile(String userId, String fullName, String email, String phone, String address, String idCard, String relativePhone) {
        return dbHelper.updateProfile(userId, fullName, email, phone, address, idCard, relativePhone);
    }

    /**
     * Lấy user đang đăng nhập hiện tại (dựa vào SessionManager + SQLite).
     * Nếu vì lý do gì đó chưa đăng nhập, trả về user khách mẫu để tránh crash UI.
     */
    @Override
    public User getCurrentUser() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            return new User("guest", "Khách", "guest@datphongks.com", "", "", 0);
        }
        User user = dbHelper.getUserById(userId);
        if (user == null) {
            return new User("guest", "Khách", "guest@datphongks.com", "", "", 0);
        }
        return user;
    }

    @Override
    public void logout() {
        sessionManager.logout();
    }
}
