package com.example.datphongks.simplebooking;

/**
 * Model tương ứng với bảng "Room" trong SQLite.
 * Gồm: id, name, price, image_res (id ảnh trong drawable), amenities (dịch vụ đi kèm),
 * status ("Trống" hoặc "Đã đặt").
 */
public class Room {

    private int id;
    private String name;
    private double price;
    private int imageRes;      // id ảnh trong res/drawable, ví dụ: R.drawable.room_1
    private String amenities;  // ví dụ: "Wifi, Điều hòa, Nước nóng"
    private double rating;
    private String imageUrl;
    private String location;
    private String description;
    private String status = "Trống"; // "Trống" hoặc "Đã đặt"

    // Constructor đầy đủ (dùng khi đọc dữ liệu từ Cursor ra, đã có id)
    public Room(int id, String name, double price, int imageRes, String amenities) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.amenities = amenities;
    }

    // Constructor không có id (dùng khi insert phòng mới, id sẽ tự tăng)
    public Room(String name, double price, int imageRes, String amenities) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.amenities = amenities;
    }

    // ----- Getter / Setter -----
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status != null ? status : "Trống";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
