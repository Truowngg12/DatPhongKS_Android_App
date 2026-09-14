# Ứng dụng Đặt phòng Khách sạn (DATPHONGKS)

Ứng dụng di động trên nền tảng Android hỗ trợ người dùng tìm kiếm, đặt phòng khách sạn, quản lý lịch sử đặt phòng và mục yêu thích. Hệ thống được tích hợp phân quyền rõ ràng, tối ưu hóa trải nghiệm cho cả Khách hàng (User) và Quản trị viên (Admin).

## Công nghệ ứng dụng

* **Ngôn ngữ & Nền tảng:** Java (Android SDK)
* **Kiến trúc hệ thống:** MVVM (Model-View-ViewModel) kết hợp Repository Pattern
* **Cơ sở dữ liệu:** SQLite (Quản lý User, Room, Booking History, Favorites)
* **Giao diện (UI/UX):** Material Design 3
* **Thư viện hỗ trợ:** Glide (Tối ưu hóa tải và hiển thị hình ảnh)

## Dự án thư mục cấu trúc

```text
DATPHONGKS/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/truowngg12/datphongks/
│   │   │   │   ├── view/         # UI Controllers (Activities, Fragments)
│   │   │   │   ├── viewmodel/    # Xử lý logic và kết nối dữ liệu cho View (MVVM)
│   │   │   │   ├── model/        # Các lớp đối tượng (User, Room, Booking...)
│   │   │   │   ├── repository/   # Pattern trung gian xử lý thao tác dữ liệu
│   │   │   │   └── database/     # Cấu hình và thao tác với SQLite Helper
│   │   │   ├── res/              # Layout XML, màu sắc, font chữ và hình ảnh
│   │   │   └── AndroidManifest.xml # File cấu hình và cấp quyền của ứng dụng

🚀 Hướng dẫn cài đặt và Khởi chạy
Yêu cầu hệ thống
Android Studio: Phiên bản Flamingo (2022.2.1) hoặc mới hơn.

JDK: Java 11 hoặc Java 17.

Thiết bị: Máy ảo (Emulator) hoặc điện thoại Android thật chạy API 24 (Android 7.0) trở lên.

1. Tải mã nguồn (Clone Project)
Mở Terminal hoặc Git Bash và chạy lệnh sau để tải dự án về máy:
git clone [[https://github.com/Truowngg12/ten-repo-cua-ban.git](https://github.com/Truowngg12/DatPhongKS_Android_App.git)]

2. Mở dự án và Đồng bộ (Sync)
Mở Android Studio.

Chọn File > Open và trỏ tới thư mục DATPHONGKS vừa tải về.

Đợi Android Studio tải các thư viện cần thiết. Nếu có thông báo Gradle Sync, hãy bấm Sync Now.

3. Cấu hình Cơ sở dữ liệu (SQLite)
Dự án sử dụng cơ sở dữ liệu cục bộ SQLite. Bạn không cần cài đặt thêm server hay cấu hình file SQL bên ngoài.

Ngay trong lần đầu tiên ứng dụng được chạy (chạy hàm onCreate của SQLiteOpenHelper), toàn bộ các bảng dữ liệu (Users, Rooms, Bookings) sẽ tự động được khởi tạo trong bộ nhớ của thiết bị.

4. Chạy ứng dụng
Kết nối điện thoại Android của bạn (đã bật chế độ USB Debugging) hoặc mở một máy ảo (AVD).

Bấm nút Run (▶) màu xanh lá cây trên thanh công cụ của Android Studio hoặc sử dụng phím tắt Shift + F10.
│   └── build.gradle              # Cấu hình thư viện (Glide, Material 3) cho module
└── build.gradle                  # Cấu hình build cho toàn bộ dự án
