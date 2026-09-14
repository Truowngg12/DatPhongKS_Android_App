package com.example.datphongks.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.datphongks.data.models.User;

/**
 * SQLiteOpenHelper CHÍNH của toàn app (khác với DatabaseHelper trong package
 * "simplebooking" - đó là DB riêng cho module demo Đặt phòng đơn giản).
 *
 * Phần 1 chỉ quản lý bảng "User":
 *   id            INTEGER PRIMARY KEY AUTOINCREMENT
 *   full_name     TEXT
 *   email         TEXT UNIQUE
 *   password      TEXT   (demo: lưu plain text - xem ghi chú bảo mật bên dưới)
 *   phone         TEXT   (MỚI theo yêu cầu)
 *   role          INTEGER  0 = User thường, 1 = Admin  (MỚI theo yêu cầu)
 *   avatar_url    TEXT
 *
 * GHI CHÚ BẢO MẬT: Đây là app đồ án/demo nên mật khẩu đang lưu dạng plain text
 * cho dễ kiểm tra. Lên production PHẢI băm bằng SHA-256/BCrypt trước khi lưu,
 * tuyệt đối không lưu password thô.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "datphongks_app.db";
    private static final int DATABASE_VERSION = 3; // Nâng version DB để bổ sung các cột thông tin mới

    private static final String TABLE_USER = "User";

    private static final String COL_ID = "id";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_PHONE = "phone";
    private static final String COL_ROLE = "role";
    private static final String COL_AVATAR_URL = "avatar_url";
    private static final String COL_ADDRESS = "address";
    private static final String COL_ID_CARD = "id_card";
    private static final String COL_RELATIVE_PHONE = "relative_phone";

    // Vai trò
    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;

    public AppDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_FULL_NAME + " TEXT, "
                + COL_EMAIL + " TEXT UNIQUE, "
                + COL_PASSWORD + " TEXT, "
                + COL_PHONE + " TEXT, "
                + COL_ROLE + " INTEGER DEFAULT 0, "
                + COL_AVATAR_URL + " TEXT, "
                + COL_ADDRESS + " TEXT, "
                + COL_ID_CARD + " TEXT, "
                + COL_RELATIVE_PHONE + " TEXT)";
        db.execSQL(createUserTable);

        // Gieo sẵn 2 tài khoản mẫu để test đăng nhập ngay:
        seedDefaultAccounts(db);
    }

    private void seedDefaultAccounts(SQLiteDatabase db) {
        try {
            ContentValues admin = new ContentValues();
            admin.put(COL_FULL_NAME, "Quản trị viên");
            admin.put(COL_EMAIL, "admin@datphongks.com");
            admin.put(COL_PASSWORD, "admin123");
            admin.put(COL_PHONE, "0900000000");
            admin.put(COL_ROLE, ROLE_ADMIN);
            admin.put(COL_AVATAR_URL, "https://i.pravatar.cc/150?u=admin");
            db.insertWithOnConflict(TABLE_USER, null, admin, SQLiteDatabase.CONFLICT_IGNORE);

            ContentValues user = new ContentValues();
            user.put(COL_FULL_NAME, "Nguyễn Văn A");
            user.put(COL_EMAIL, "user@datphongks.com");
            user.put(COL_PASSWORD, "user123");
            user.put(COL_PHONE, "0911111111");
            user.put(COL_ROLE, ROLE_USER);
            user.put(COL_AVATAR_URL, "https://i.pravatar.cc/150?u=u1");
            db.insertWithOnConflict(TABLE_USER, null, user, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    /**
     * Đăng ký tài khoản User mới (role luôn = 0, không cho tự đăng ký làm Admin).
     * @return id vừa tạo, hoặc -1 nếu email đã tồn tại / lỗi.
     */
    public long registerUser(String fullName, String email, String password, String phone) {
        if (isEmailExists(email)) {
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_FULL_NAME, fullName);
            values.put(COL_EMAIL, email != null ? email.trim().toLowerCase() : "");
            values.put(COL_PASSWORD, password);
            values.put(COL_PHONE, phone);
            values.put(COL_ROLE, ROLE_USER);
            values.put(COL_AVATAR_URL, "https://i.pravatar.cc/150?u=" + email);
            result = db.insert(TABLE_USER, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return result;
    }

    /** Kiểm tra đăng nhập. Trả về User nếu đúng, null nếu sai email/mật khẩu. */
    public User checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        User user = null;
        try {
            String cleanEmail = email != null ? email.trim().toLowerCase() : "";
            cursor = db.query(
                    TABLE_USER,
                    null,
                    "LOWER(" + COL_EMAIL + ") = ? AND " + COL_PASSWORD + " = ?",
                    new String[]{cleanEmail, password},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return user;
    }

    /** Kiểm tra email đã được đăng ký hay chưa. */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            String cleanEmail = email != null ? email.trim().toLowerCase() : "";
            cursor = db.query(TABLE_USER, new String[]{COL_ID},
                    "LOWER(" + COL_EMAIL + ") = ?", new String[]{cleanEmail}, null, null, null);
            exists = cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return exists;
    }

    /** Lấy thông tin 1 User theo id. */
    public User getUserById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        User user = null;
        try {
            cursor = db.query(TABLE_USER, null, COL_ID + " = ?",
                    new String[]{id}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return user;
    }

    /** Cập nhật thông tin cá nhân mở rộng (Tên, Email, SĐT, Địa chỉ, CMND, SĐT Người thân) */
    public boolean updateProfile(String id, String fullName, String email, String phone, String address, String idCard, String relativePhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_FULL_NAME, fullName);
            values.put(COL_EMAIL, email != null ? email.trim().toLowerCase() : "");
            values.put(COL_PHONE, phone);
            values.put(COL_ADDRESS, address);
            values.put(COL_ID_CARD, idCard);
            values.put(COL_RELATIVE_PHONE, relativePhone);
            rows = db.update(TABLE_USER, values, COL_ID + " = ?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rows > 0;
    }

    public boolean updateProfile(String id, String fullName, String email, String phone) {
        return updateProfile(id, fullName, email, phone, "", "", "");
    }

    private User cursorToUser(Cursor cursor) {
        int idIdx = cursor.getColumnIndex(COL_ID);
        int nameIdx = cursor.getColumnIndex(COL_FULL_NAME);
        int emailIdx = cursor.getColumnIndex(COL_EMAIL);
        int phoneIdx = cursor.getColumnIndex(COL_PHONE);
        int avatarIdx = cursor.getColumnIndex(COL_AVATAR_URL);
        int roleIdx = cursor.getColumnIndex(COL_ROLE);
        int addressIdx = cursor.getColumnIndex(COL_ADDRESS);
        int idCardIdx = cursor.getColumnIndex(COL_ID_CARD);
        int relPhoneIdx = cursor.getColumnIndex(COL_RELATIVE_PHONE);

        String id = (idIdx != -1) ? String.valueOf(cursor.getInt(idIdx)) : "0";
        String fullName = (nameIdx != -1) ? cursor.getString(nameIdx) : "";
        String email = (emailIdx != -1) ? cursor.getString(emailIdx) : "";
        String phone = (phoneIdx != -1) ? cursor.getString(phoneIdx) : "";
        String avatarUrl = (avatarIdx != -1) ? cursor.getString(avatarIdx) : "";
        int role = (roleIdx != -1) ? cursor.getInt(roleIdx) : ROLE_USER;
        String address = (addressIdx != -1) ? cursor.getString(addressIdx) : "";
        String idCard = (idCardIdx != -1) ? cursor.getString(idCardIdx) : "";
        String relativePhone = (relPhoneIdx != -1) ? cursor.getString(relPhoneIdx) : "";

        if (fullName == null) fullName = "";
        if (email == null) email = "";
        if (phone == null) phone = "";
        if (avatarUrl == null) avatarUrl = "";
        if (address == null) address = "";
        if (idCard == null) idCard = "";
        if (relativePhone == null) relativePhone = "";

        return new User(id, fullName, email, phone, avatarUrl, role, address, idCard, relativePhone);
    }
}

