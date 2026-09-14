package com.example.datphongks.simplebooking;

/**
 * Model tương ứng với bảng "Booking" (lịch sử đặt phòng) trong SQLite.
 * Gồm: id, room_id (khóa ngoại tới Room), customer_name, customer_phone,
 * rent_days (số ngày thuê), total_price (tổng tiền = price * rent_days).
 */
public class Booking {

    private int id;
    private int roomId;
    private String customerName;
    private String customerPhone;
    private int rentDays;
    private double totalPrice;

    // Constructor đầy đủ (dùng khi đọc dữ liệu từ Cursor ra, đã có id)
    public Booking(int id, int roomId, String customerName, String customerPhone,
                    int rentDays, double totalPrice) {
        this.id = id;
        this.roomId = roomId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.rentDays = rentDays;
        this.totalPrice = totalPrice;
    }

    // Constructor không có id (dùng khi tạo booking mới để insert vào DB)
    public Booking(int roomId, String customerName, String customerPhone,
                    int rentDays, double totalPrice) {
        this.roomId = roomId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.rentDays = rentDays;
        this.totalPrice = totalPrice;
    }

    // ----- Getter / Setter -----
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getRentDays() {
        return rentDays;
    }

    public void setRentDays(int rentDays) {
        this.rentDays = rentDays;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
