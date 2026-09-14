package com.example.datphongks.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datphongks.R;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.simplebooking.DatabaseHelper;
import com.example.datphongks.simplebooking.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<Hotel>> filteredHotels = new MutableLiveData<>();

    private String currentQuery = "";
    private float minPrice = 0;
    private float maxPrice = 2000000; // Tăng khoảng giá cho phù hợp tiền VND
    private float minRating = 0;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        this.dbHelper = new DatabaseHelper(application);
        performSearch();
    }

    public LiveData<List<Hotel>> getFilteredHotels() {
        return filteredHotels;
    }

    public void setSearchQuery(String query) {
        this.currentQuery = query;
        performSearch();
    }

    public void setPriceRange(float min, float max) {
        this.minPrice = min;
        this.maxPrice = max;
        performSearch();
    }

    public void setStars(List<Integer> stars) {
        if (!stars.isEmpty()) {
            // Lấy số sao nhỏ nhất trong danh sách chọn để làm minRating
            int min = 5;
            for (int s : stars) if (s < min) min = s;
            this.minRating = min;
        } else {
            this.minRating = 0;
        }
        performSearch();
    }

    public void setAmenities(List<String> amenities) {
        // SQLite trong demo này chưa lọc sâu theo từng amenity trong text, 
        // sẽ cải tiến nếu cần ở các phần sau.
        performSearch();
    }

    private void performSearch() {
        List<Room> rooms = dbHelper.searchRooms(currentQuery, minPrice, maxPrice, minRating);
        
        List<Hotel> mappedHotels = new ArrayList<>();
        String[] beautifulImages = {
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=600&q=80"
        };

        int index = 0;
        for (Room r : rooms) {
            String imageUrl = (r.getImageUrl() != null && !r.getImageUrl().isEmpty())
                    ? r.getImageUrl()
                    : beautifulImages[index % beautifulImages.length];
            
            mappedHotels.add(new Hotel(
                    String.valueOf(r.getId()),
                    r.getName(),
                    "Tiện ích: " + r.getAmenities(),
                    imageUrl,
                    (float) r.getRating(),
                    (int) r.getPrice(),
                    dbHelper.isFavorite(r.getId()),
                    Arrays.asList("WiFi", "Tivi")
            ));
            index++;
        }
        filteredHotels.setValue(mappedHotels);
    }
}
