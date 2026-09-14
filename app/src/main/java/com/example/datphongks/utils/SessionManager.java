package com.example.datphongks.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.datphongks.data.models.User;

/**
 * Quản lý phiên đăng nhập hiện tại bằng SharedPreferences.
 * Dùng để: sau khi Login/Register thành công thì lưu lại userId + role,
 * các màn hình khác (Profile, kiểm tra quyền Admin...) đọc lại từ đây
 * mà không cần truyền Intent lòng vòng.
 */
public class SessionManager {

    private static final String PREF_NAME = "datphongks_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** Gọi sau khi đăng nhập / đăng ký thành công. */
    public void login(User user) {
        prefs.edit()
                .putString(KEY_USER_ID, user.getId())
                .putInt(KEY_ROLE, user.getRole())
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    /** Gọi khi bấm Đăng xuất. */
    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public int getRole() {
        return prefs.getInt(KEY_ROLE, 0);
    }

    public boolean isAdmin() {
        return isLoggedIn() && getRole() == 1;
    }
}
