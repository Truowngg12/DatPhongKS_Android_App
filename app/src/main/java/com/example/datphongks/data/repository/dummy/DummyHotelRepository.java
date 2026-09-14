package com.example.datphongks.data.repository.dummy;

import com.example.datphongks.data.models.Category;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.data.models.Room;
import com.example.datphongks.data.repository.HotelRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DummyHotelRepository implements HotelRepository {

    private List<Hotel> hotels;

    public DummyHotelRepository() {
        hotels = new ArrayList<>();
        hotels.add(new Hotel("1", "Khách Sạn Grand Riviera", "Hà Nội, Việt Nam", "https://picsum.photos/seed/1/800/600", 4.9f, 450000, true, Arrays.asList("Hồ bơi", "Spa", "Gym", "WiFi")));
        hotels.add(new Hotel("2", "Khu Dưỡng Ocean Breeze", "Phú Quốc, Việt Nam", "https://picsum.photos/seed/2/800/600", 4.8f, 600000, false, Arrays.asList("Bãi biển", "Hồ bơi", "Trọn gói")));
        hotels.add(new Hotel("3", "Khách Sạn Mountain View", "Sa Pa, Việt Nam", "https://picsum.photos/seed/3/800/600", 4.7f, 320000, false, Arrays.asList("Ngắm núi", "Lò sưởi", "Spa")));
        hotels.add(new Hotel("4", "Khách Sạn Urban Oasis", "TP. Hồ Chí Minh, Việt Nam", "https://picsum.photos/seed/4/800/600", 4.6f, 280000, true, Arrays.asList("View thành phố", "Gần trung tâm", "Gym")));
        hotels.add(new Hotel("5", "Khách Sạn Cung Điện Hoàng Gia", "Đà Nẵng, Việt Nam", "https://picsum.photos/seed/5/800/600", 5.0f, 850000, false, Arrays.asList("Sang trọng", "Phục vụ 24/7", "Hồ bơi")));
        hotels.add(new Hotel("6", "Khách Sạn Heritage Inn", "Phố Cổ, Hà Nội", "https://picsum.photos/seed/6/800/600", 4.5f, 150000, true, Arrays.asList("Phố cổ", "Ăn sáng", "WiFi")));
        hotels.add(new Hotel("7", "Khu Resort Biển Xanh", "Nha Trang, Việt Nam", "https://picsum.photos/seed/7/800/600", 4.8f, 400000, false, Arrays.asList("View biển", "Hồ bơi vô cực", "Tắm nắng")));
        hotels.add(new Hotel("8", "Khách Sạn Mộng Mơ", "Đà Lạt, Việt Nam", "https://picsum.photos/seed/8/800/600", 4.7f, 220000, true, Arrays.asList("Vườn hoa", "View đồi thông", "Yoga")));
    }

    @Override
    public List<Hotel> getHotels() {
        return hotels;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Khách Sạn Nổi Bật", true));
        categories.add(new Category("2", "Ưu Đãi Hot", false));
        categories.add(new Category("3", "Đánh Giá Cao", false));
        return categories;
    }

    @Override
    public Hotel getHotelById(String id) {
        for (Hotel hotel : hotels) {
            if (hotel.getId().equals(id)) return hotel;
        }
        return null;
    }

    @Override
    public List<Room> getRoomsForHotel(String hotelId) {
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("r1", hotelId, "Phòng Cao Cấp (Deluxe)", "Phòng rộng rãi với giường lớn và tầm nhìn thành phố tuyệt đẹp.", 200000, 2, "https://picsum.photos/seed/r1/400/300"));
        rooms.add(new Room("r2", hotelId, "Phòng Hoàng Gia VIP", "Căn hộ sang trọng với phòng khách riêng biệt và ban công hướng biển.", 450000, 2, "https://picsum.photos/seed/r2/400/300"));
        rooms.add(new Room("r3", hotelId, "Phòng Gia Đình", "Phòng lớn với 2 giường đôi, phù hợp cho gia đình 4 người.", 350000, 4, "https://picsum.photos/seed/r3/400/300"));
        return rooms;
    }

    @Override
    public void toggleFavorite(String hotelId) {
        Hotel hotel = getHotelById(hotelId);
        if (hotel != null) {
            hotel.setFavorite(!hotel.isFavorite());
        }
    }

    @Override
    public List<Hotel> getFavoriteHotels() {
        List<Hotel> favorites = new ArrayList<>();
        for (Hotel hotel : hotels) {
            if (hotel.isFavorite()) favorites.add(hotel);
        }
        return favorites;
    }
}