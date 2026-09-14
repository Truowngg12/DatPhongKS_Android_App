package com.example.datphongks.simplebooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý Cơ sở dữ liệu SQLite cho module Đặt phòng đơn giản.
 * Đặt tên riêng (DATPHONGKS_SIMPLE.db) để KHÔNG đụng tới dữ liệu/DB nào
 * mà phần code cũ (MVVM/Repository) của project đang dùng.
 *
 * Quản lý 2 bảng: Room (danh sách phòng) và Booking (lịch sử đặt phòng).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // ----- Thông tin Database -----
    private static final String DATABASE_NAME = "datphongks_simple.db";
    private static final int DATABASE_VERSION = 5; // Bump version để tự động nạp 10+ phòng mẫu mới vào SQLite

    // ----- Tên bảng -----
    private static final String TABLE_ROOM = "Room";
    private static final String TABLE_BOOKING = "Booking";
    private static final String TABLE_FAVORITES = "Favorites";

    // ----- Cột bảng Room -----
    private static final String ROOM_ID = "id";
    private static final String ROOM_NAME = "name";
    private static final String ROOM_PRICE = "price";
    private static final String ROOM_IMAGE_RES = "image_res";
    private static final String ROOM_IMAGE_URL = "image_url";
    private static final String ROOM_AMENITIES = "amenities";
    private static final String ROOM_STATUS = "room_status"; // "Trống", "Đã đặt", "Bảo trì"
    private static final String ROOM_RATING = "rating";
    private static final String ROOM_LOCATION = "location";
    private static final String ROOM_DESCRIPTION = "description";

    // ----- Cột bảng Favorites -----
    private static final String FAV_ID = "id";
    private static final String FAV_ROOM_ID = "room_id";

    // ----- Cột bảng Booking -----
    private static final String BOOKING_ID = "id";
    private static final String BOOKING_ROOM_ID = "room_id";
    private static final String BOOKING_CUSTOMER_NAME = "customer_name";
    private static final String BOOKING_CUSTOMER_PHONE = "customer_phone";
    private static final String BOOKING_RENT_DAYS = "rent_days";
    private static final String BOOKING_TOTAL_PRICE = "total_price";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Được gọi 1 lần đầu tiên khi Database được tạo ra
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Room
        String createRoomTable = "CREATE TABLE " + TABLE_ROOM + " ("
                + ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ROOM_NAME + " TEXT, "
                + ROOM_PRICE + " REAL, "
                + ROOM_IMAGE_RES + " INTEGER, "
                + ROOM_IMAGE_URL + " TEXT, "
                + ROOM_AMENITIES + " TEXT, "
                + ROOM_STATUS + " TEXT DEFAULT 'Trống', "
                + ROOM_RATING + " REAL DEFAULT 4.5, "
                + ROOM_LOCATION + " TEXT, "
                + ROOM_DESCRIPTION + " TEXT)";
        db.execSQL(createRoomTable);

        // Tạo bảng Booking
        String createBookingTable = "CREATE TABLE " + TABLE_BOOKING + " ("
                + BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BOOKING_ROOM_ID + " INTEGER, "
                + BOOKING_CUSTOMER_NAME + " TEXT, "
                + BOOKING_CUSTOMER_PHONE + " TEXT, "
                + BOOKING_RENT_DAYS + " INTEGER, "
                + BOOKING_TOTAL_PRICE + " REAL)";
        db.execSQL(createBookingTable);

        // Tạo bảng Favorites
        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + " ("
                + FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FAV_ROOM_ID + " INTEGER UNIQUE)";
        db.execSQL(createFavoritesTable);

        // Chèn sẵn vài dữ liệu mẫu vào bảng Room để có dữ liệu hiển thị ngay khi chạy app
        insertSampleRooms(db);
    }

    // Được gọi khi DATABASE_VERSION tăng lên (nâng cấp CSDL) - ở đây đơn giản là xóa và tạo lại
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
        onCreate(db);
    }

    /**
     * Chèn vài dữ liệu mẫu (gọi 1 lần trong onCreate).
     * LƯU Ý: image_res đang tạm để 0 vì Java (module logic) không được phép
     * biết trước R.drawable của người vẽ giao diện. Người vẽ layout hãy:
     *  - Thêm ảnh phòng vào res/drawable (ví dụ: room_1, room_2, room_3)
     *  - Sửa lại 3 dòng insertRoom bên dưới, thay số 0 bằng R.drawable.room_1, room_2, room_3
     */
    private void insertSampleRooms(SQLiteDatabase db) {
        addRoomInternal(db, "Phòng Deluxe Ban Công Hướng Biển", 350000, 0, "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80", "Wifi, Điều hòa, Ban công hướng biển, Bồn tắm", "Trống", "Phú Quốc, Việt Nam", "Phòng Deluxe sang trọng view biển ngắm hoàng hôn cực đẹp.", 4.8);
        addRoomInternal(db, "Phòng Executive Suite Hoàng Gia", 650000, 0, "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=600&q=80", "Wifi, Bếp riêng, Bồn Jacuzzi, View phố cổ", "Đã đặt", "Hà Nội, Việt Nam", "Căn hộ Suite đẳng cấp dành cho thương gia và cặp đôi.", 5.0);
        addRoomInternal(db, "Phòng Mây Ngắm Đỉnh Fansipan", 280000, 0, "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=600&q=80", "Wifi, Lò sưởi, View thung lũng mây, Ban công", "Trống", "Sa Pa, Việt Nam", "Không gian ấm cúng ngập tràn không khí vùng cao Sa Pa.", 4.7);
        addRoomInternal(db, "Phòng Family Đôi Rộng Rãi", 450000, 0, "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=600&q=80", "Wifi, 2 Giường đôi, Bể bơi riêng, Sân chơi", "Trống", "Đà Nẵng, Việt Nam", "Phòng gia đình tiện nghi đầy đủ cho 4-6 người lưu trú.", 4.9);
        addRoomInternal(db, "Villa Biển Xanh Hồ Bơi Vô Cực", 1200000, 0, "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=600&q=80", "Wifi, Bể bơi vô cực, Bãi biển riêng, Phục vụ 24/7", "Đã đặt", "Nha Trang, Việt Nam", "Biệt thự sát biển với hồ bơi riêng tư đẳng cấp 5 sao.", 5.0);
        addRoomInternal(db, "Phòng Penthouse Thượng Uyển 360", 950000, 0, "https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=600&q=80", "Wifi, Ban công 360, Bar riêng, Bồn tắm panorama", "Trống", "TP. Hồ Chí Minh, Việt Nam", "Penthouse tầng cao với tầm nhìn ôm trọn thành phố về đêm.", 4.9);
        addRoomInternal(db, "Bungalow Gỗ Đồi Thông Mộng Mơ", 320000, 0, "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?auto=format&fit=crop&w=600&q=80", "Wifi, Lò sưởi, Vườn hoa, View đồi thông", "Trống", "Đà Lạt, Việt Nam", "Bungalow mộc mạc giữa rừng thông thơ mộng Đà Lạt.", 4.8);
        addRoomInternal(db, "Phòng Studio Hiện Đại Trung Tâm", 220000, 0, "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=600&q=80", "Wifi, Máy giặt, Bếp ăn, Điều hòa", "Trống", "Hà Nội, Việt Nam", "Phòng Studio khép kín phù hợp cho khách công tác.", 4.6);
        addRoomInternal(db, "Phòng Ocean View Ban Công Sunset", 400000, 0, "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=600&q=80", "Wifi, View hoàng hôn, Ghế tắm nắng, Bar", "Trống", "Vũng Tàu, Việt Nam", "Tận hưởng làn gió biển và cảnh hoàng hôn quyến rũ.", 4.7);
        addRoomInternal(db, "Phòng Tổng Thống Presidential VIP", 2500000, 0, "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=600&q=80", "Wifi, Quản gia riêng, Xe đưa đón, Bồn dát vàng", "Trống", "Hà Nội, Việt Nam", "Phòng Presidential xa hoa bậc nhất dành cho thượng khách.", 5.0);
        addRoomInternal(db, "Phòng Standard Tiết Kiệm", 180000, 0, "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=600&q=80", "Wifi, Điều hòa, Nước nóng, Ăn sáng", "Trống", "Cần Thơ, Việt Nam", "Phòng tiêu chuẩn sạch sẽ, đầy đủ tiện nghi với giá tốt.", 4.5);
    }

    private void addRoomInternal(SQLiteDatabase db, String name, double price, int imageRes, String imageUrl, String amenities, String status, String location, String description, double rating) {
        ContentValues values = new ContentValues();
        values.put(ROOM_NAME, name);
        values.put(ROOM_PRICE, price);
        values.put(ROOM_IMAGE_RES, imageRes);
        values.put(ROOM_IMAGE_URL, imageUrl);
        values.put(ROOM_AMENITIES, amenities);
        values.put(ROOM_STATUS, status);
        values.put(ROOM_LOCATION, location);
        values.put(ROOM_DESCRIPTION, description);
        values.put(ROOM_RATING, rating);
        db.insert(TABLE_ROOM, null, values);
    }

    // Hàm insert dùng nội bộ trong onCreate (nhận sẵn đối tượng SQLiteDatabase)
    private void insertRoomInternal(SQLiteDatabase db, String name, double price,
                                     int imageRes, String amenities, double rating) {
        addRoomInternal(db, name, price, imageRes, "", amenities, "Trống", "Hà Nội, Việt Nam", "Mô tả phòng", rating);
    }

    /**
     * Thêm 1 phòng mới vào bảng Room.
     * @return id (dạng long) của dòng vừa được thêm, -1 nếu lỗi.
     */
    public long insertRoom(Room room) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROOM_NAME, room.getName());
        values.put(ROOM_PRICE, room.getPrice());
        values.put(ROOM_IMAGE_RES, room.getImageRes());
        values.put(ROOM_AMENITIES, room.getAmenities());
        long result = db.insert(TABLE_ROOM, null, values);
        db.close();
        return result;
    }

    /**
     * Lấy toàn bộ danh sách phòng từ bảng Room.
     * @return List<Room> để đổ vào ListView thông qua RoomAdapter.
     */
    private Room cursorToRoom(Cursor cursor) {
        int idIdx = cursor.getColumnIndex(ROOM_ID);
        int nameIdx = cursor.getColumnIndex(ROOM_NAME);
        int priceIdx = cursor.getColumnIndex(ROOM_PRICE);
        int imgResIdx = cursor.getColumnIndex(ROOM_IMAGE_RES);
        int imgUrlIdx = cursor.getColumnIndex(ROOM_IMAGE_URL);
        int amenitiesIdx = cursor.getColumnIndex(ROOM_AMENITIES);
        int ratingIdx = cursor.getColumnIndex(ROOM_RATING);
        int locIdx = cursor.getColumnIndex(ROOM_LOCATION);
        int descIdx = cursor.getColumnIndex(ROOM_DESCRIPTION);
        int statusIdx = cursor.getColumnIndex(ROOM_STATUS);

        int id = (idIdx != -1) ? cursor.getInt(idIdx) : 0;
        String name = (nameIdx != -1) ? cursor.getString(nameIdx) : "Phòng";
        double price = (priceIdx != -1) ? cursor.getDouble(priceIdx) : 0.0;
        int imageRes = (imgResIdx != -1) ? cursor.getInt(imgResIdx) : 0;
        String imageUrl = (imgUrlIdx != -1) ? cursor.getString(imgUrlIdx) : "";
        String amenities = (amenitiesIdx != -1) ? cursor.getString(amenitiesIdx) : "";
        double rating = (ratingIdx != -1) ? cursor.getDouble(ratingIdx) : 4.5;
        String status = (statusIdx != -1) ? cursor.getString(statusIdx) : "Trống";
        if (status == null || status.isEmpty()) status = "Trống";

        Room room = new Room(id, name, price, imageRes, amenities);
        room.setImageUrl(imageUrl);
        room.setRating(rating);
        room.setStatus(status);

        if (locIdx != -1) room.setLocation(cursor.getString(locIdx));
        if (descIdx != -1) room.setDescription(cursor.getString(descIdx));

        return room;
    }

    public List<Room> getAllRooms() {
        return getRoomsFiltered(null);
    }

    public List<Room> getRoomsByStatus(String status) {
        if (status == null || status.isEmpty() || "ALL".equalsIgnoreCase(status)) {
            return getAllRooms();
        }
        return getRoomsFiltered(ROOM_STATUS + " = '" + status + "'");
    }

    public List<Room> getRoomsFiltered(String selection) {
        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_ROOM,
                    null,
                    selection,
                    null, null, null,
                    ROOM_ID + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    roomList.add(cursorToRoom(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return roomList;
    }

    public List<Room> getHotDeals() {
        return getRoomsFiltered(ROOM_PRICE + " < 300000");
    }

    public List<Room> getTopRated() {
        return getRoomsFiltered(ROOM_RATING + " >= 4.8");
    }

    /**
     * BỘ LỌC NÂNG CAO CHUẨN SENIOR
     * @param star Số sao tối thiểu (0-5)
     * @param amenity Tiện ích cần tìm (Chuỗi keyword)
     * @param minPrice Giá thấp nhất
     * @param maxPrice Giá cao nhất
     */
    public List<Room> filterRooms(int star, String amenity, int minPrice, int maxPrice) {
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        // Điều kiện 1: Giá
        selection.append(ROOM_PRICE).append(" >= ? AND ").append(ROOM_PRICE).append(" <= ?");
        selectionArgs.add(String.valueOf(minPrice));
        selectionArgs.add(String.valueOf(maxPrice));

        // Điều kiện 2: Số sao
        if (star > 0) {
            selection.append(" AND ").append(ROOM_RATING).append(" >= ?");
            selectionArgs.add(String.valueOf(star));
        }

        // Điều kiện 3: Tiện ích (dùng LIKE để tìm keyword trong chuỗi tiện ích)
        if (amenity != null && !amenity.isEmpty()) {
            selection.append(" AND ").append(ROOM_AMENITIES).append(" LIKE ?");
            selectionArgs.add("%" + amenity + "%");
        }

        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_ROOM,
                    null,
                    selection.toString(),
                    selectionArgs.toArray(new String[0]),
                    null, null,
                    ROOM_ID + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    roomList.add(cursorToRoom(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return roomList;
    }

    /**
     * Tìm kiếm nâng cao: Tên (LIKE), khoảng giá, số sao tối thiểu.
     */
    public List<Room> searchRooms(String query, double minPrice, double maxPrice, double minRating) {
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        selection.append(ROOM_PRICE + " >= ? AND " + ROOM_PRICE + " <= ?");
        selectionArgs.add(String.valueOf(minPrice));
        selectionArgs.add(String.valueOf(maxPrice));

        if (minRating > 0) {
            selection.append(" AND " + ROOM_RATING + " >= ?");
            selectionArgs.add(String.valueOf(minRating));
        }

        if (query != null && !query.isEmpty()) {
            selection.append(" AND " + ROOM_NAME + " LIKE ?");
            selectionArgs.add("%" + query + "%");
        }

        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_ROOM,
                    null,
                    selection.toString(),
                    selectionArgs.toArray(new String[0]),
                    null, null,
                    ROOM_ID + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    roomList.add(cursorToRoom(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return roomList;
    }

    // --- FAVORITES LOGIC ---
    public void toggleFavorite(int roomId) {
        if (isFavorite(roomId)) {
            removeFavorite(roomId);
        } else {
            addFavorite(roomId);
        }
    }

    public void addFavorite(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAV_ROOM_ID, roomId);
        db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void removeFavorite(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, FAV_ROOM_ID + " = ?", new String[]{String.valueOf(roomId)});
        db.close();
    }

    public boolean isFavorite(int roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, null, FAV_ROOM_ID + " = ?", new String[]{String.valueOf(roomId)}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    /**
     * Thêm 1 lượt đặt phòng mới vào bảng Booking.
     * Tự động cập nhật trạng thái phòng sang "Đã đặt".
     * @return id (dạng long) của dòng vừa được thêm, -1 nếu lỗi.
     */
    public long insertBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BOOKING_ROOM_ID, booking.getRoomId());
        values.put(BOOKING_CUSTOMER_NAME, booking.getCustomerName());
        values.put(BOOKING_CUSTOMER_PHONE, booking.getCustomerPhone());
        values.put(BOOKING_RENT_DAYS, booking.getRentDays());
        values.put(BOOKING_TOTAL_PRICE, booking.getTotalPrice());
        long result = db.insert(TABLE_BOOKING, null, values);

        if (result != -1 && booking.getRoomId() > 0) {
            ContentValues roomValues = new ContentValues();
            roomValues.put(ROOM_STATUS, "Đã đặt");
            db.update(TABLE_ROOM, roomValues, ROOM_ID + " = ?", new String[]{String.valueOf(booking.getRoomId())});
        }

        db.close();
        return result;
    }

    // --- ADMIN STATISTICS REAL-TIME ---
    public int getRoomCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOM, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public int getOccupiedRoomCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOM + " WHERE " + ROOM_STATUS + " = ?", new String[]{"Đã đặt"});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public int getAvailableRoomCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOM + " WHERE " + ROOM_STATUS + " != ? OR " + ROOM_STATUS + " IS NULL", new String[]{"Đã đặt"});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public int getBookingCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKING, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + BOOKING_TOTAL_PRICE + ") FROM " + TABLE_BOOKING, null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        db.close();
        return total;
    }

    public void toggleRoomStatus(int roomId) {
        Room room = getRoomById(roomId);
        if (room != null) {
            String newStatus = "Đã đặt".equalsIgnoreCase(room.getStatus()) ? "Trống" : "Đã đặt";
            updateRoomStatus(roomId, newStatus);
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_BOOKING, null, null, null, null, null, BOOKING_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex(BOOKING_ID);
                int roomIdx = cursor.getColumnIndex(BOOKING_ROOM_ID);
                int nameIdx = cursor.getColumnIndex(BOOKING_CUSTOMER_NAME);
                int phoneIdx = cursor.getColumnIndex(BOOKING_CUSTOMER_PHONE);
                int daysIdx = cursor.getColumnIndex(BOOKING_RENT_DAYS);
                int priceIdx = cursor.getColumnIndex(BOOKING_TOTAL_PRICE);

                do {
                    int id = (idIdx != -1) ? cursor.getInt(idIdx) : 0;
                    int roomId = (roomIdx != -1) ? cursor.getInt(roomIdx) : 0;
                    String name = (nameIdx != -1) ? cursor.getString(nameIdx) : "";
                    String phone = (phoneIdx != -1) ? cursor.getString(phoneIdx) : "";
                    int days = (daysIdx != -1) ? cursor.getInt(daysIdx) : 1;
                    double price = (priceIdx != -1) ? cursor.getDouble(priceIdx) : 0.0;

                    list.add(new Booking(id, roomId, name, phone, days, price));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    // Hàm Admin: Cập nhật trạng thái phòng
    public void updateRoomStatus(int roomId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROOM_STATUS, newStatus);
        db.update(TABLE_ROOM, values, ROOM_ID + " = ?", new String[]{String.valueOf(roomId)});
        db.close();
    }

    // Hàm Admin: Xóa phòng
    public void deleteRoom(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROOM, ROOM_ID + " = ?", new String[]{String.valueOf(roomId)});
        db.close();
    }

    public Room getRoomById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Room room = null;
        try {
            cursor = db.query(TABLE_ROOM, null, ROOM_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                room = cursorToRoom(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return room;
    }

    public long addRoom(String name, int price, int imageRes, String imageUrl, String amenities, String status, String location, String description, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROOM_NAME, name);
        values.put(ROOM_PRICE, price);
        values.put(ROOM_IMAGE_RES, imageRes);
        values.put(ROOM_IMAGE_URL, imageUrl);
        values.put(ROOM_AMENITIES, amenities);
        values.put(ROOM_STATUS, status);
        values.put(ROOM_LOCATION, location);
        values.put(ROOM_DESCRIPTION, description);
        values.put(ROOM_RATING, rating);
        long result = db.insert(TABLE_ROOM, null, values);
        db.close();
        return result;
    }

    public long addRoom(String name, int price, int imageRes, String imageUrl, String amenities, String status) {
        return addRoom(name, price, imageRes, imageUrl, amenities, status, "Hà Nội, Việt Nam", "Phòng sang trọng với góc nhìn đẹp, tiện nghi cao cấp.", 4.5);
    }
}
