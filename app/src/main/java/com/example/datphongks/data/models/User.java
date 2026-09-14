package com.example.datphongks.data.models;

/**
 * Model User.
 * ĐÃ CẬP NHẬT: Thêm các trường thông tin cá nhân mở rộng:
 *  - address        : Địa chỉ
 *  - idCard         : Số CMND/CCCD
 *  - relativePhone  : Số điện thoại người thân
 */
public class User {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private int role; // 0 = User, 1 = Admin
    private String address;
    private String idCard;
    private String relativePhone;

    // Constructor đầy đủ nhất
    public User(String id, String fullName, String email, String phone, String avatarUrl, int role, String address, String idCard, String relativePhone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone != null ? phone : "";
        this.avatarUrl = avatarUrl != null ? avatarUrl : "";
        this.role = role;
        this.address = address != null ? address : "";
        this.idCard = idCard != null ? idCard : "";
        this.relativePhone = relativePhone != null ? relativePhone : "";
    }

    public User(String id, String fullName, String email, String phone, String avatarUrl, int role) {
        this(id, fullName, email, phone, avatarUrl, role, "", "", "");
    }

    public User(String id, String fullName, String email, String avatarUrl) {
        this(id, fullName, email, "", avatarUrl, 0, "", "", "");
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getRole() { return role; }
    public String getAddress() { return address; }
    public String getIdCard() { return idCard; }
    public String getRelativePhone() { return relativePhone; }

    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setRole(int role) { this.role = role; }
    public void setAddress(String address) { this.address = address; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public void setRelativePhone(String relativePhone) { this.relativePhone = relativePhone; }

    public boolean isAdmin() {
        return role == 1;
    }
}
