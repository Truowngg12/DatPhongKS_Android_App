package com.example.datphongks.data.repository;

import com.example.datphongks.data.models.Category;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.data.models.Room;

import java.util.List;

public interface HotelRepository {
    List<Hotel> getHotels();
    List<Category> getCategories();
    Hotel getHotelById(String id);
    List<Room> getRoomsForHotel(String hotelId);
    void toggleFavorite(String hotelId);
    List<Hotel> getFavoriteHotels();
}