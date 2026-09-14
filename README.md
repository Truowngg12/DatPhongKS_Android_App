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
│   └── build.gradle              # Cấu hình thư viện (Glide, Material 3) cho module
└── build.gradle                  # Cấu hình build cho toàn bộ dự án
