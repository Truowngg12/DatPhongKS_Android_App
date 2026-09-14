package com.example.datphongks.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datphongks.data.models.Category;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.data.repository.HotelRepository;
import com.example.datphongks.data.repository.dummy.DummyHotelRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final HotelRepository repository;
    private final MutableLiveData<List<Hotel>> hotels = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public HomeViewModel() {
        // In a real app, use Dependency Injection (like Hilt)
        this.repository = new DummyHotelRepository();
        loadData();
    }

    private void loadData() {
        hotels.setValue(repository.getHotels());
        categories.setValue(repository.getCategories());
    }

    public LiveData<List<Hotel>> getHotels() { return hotels; }
    public LiveData<List<Category>> getCategories() { return categories; }

    public void toggleFavorite(String hotelId) {
        repository.toggleFavorite(hotelId);
        hotels.setValue(repository.getHotels());
    }
}