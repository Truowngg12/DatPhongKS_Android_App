package com.example.datphongks.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.data.models.Room;
import com.example.datphongks.data.repository.HotelRepository;
import com.example.datphongks.data.repository.dummy.DummyHotelRepository;

import java.util.List;

public class HotelDetailViewModel extends ViewModel {

    private final HotelRepository repository;
    private final MutableLiveData<Hotel> hotel = new MutableLiveData<>();
    private final MutableLiveData<List<Room>> rooms = new MutableLiveData<>();

    public HotelDetailViewModel() {
        this.repository = new DummyHotelRepository();
    }

    public void loadHotelDetails(String hotelId) {
        hotel.setValue(repository.getHotelById(hotelId));
        rooms.setValue(repository.getRoomsForHotel(hotelId));
    }

    public LiveData<Hotel> getHotel() { return hotel; }
    public LiveData<List<Room>> getRooms() { return rooms; }
}