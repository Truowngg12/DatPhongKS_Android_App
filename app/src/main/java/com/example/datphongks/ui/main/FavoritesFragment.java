package com.example.datphongks.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.databinding.FragmentFavoritesBinding;
import com.example.datphongks.simplebooking.DatabaseHelper;
import com.example.datphongks.simplebooking.Room;
import com.example.datphongks.ui.adapters.FavoriteAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteAdapter.OnHotelClickListener {

    private FragmentFavoritesBinding binding;
    private FavoriteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    // BẮT BUỘC: Đưa lệnh loadFavorites() vào trong onResume()
    // Đảm bảo dữ liệu làm mới liên tục khi người dùng chuyển lại tab Yêu thích
    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupRecyclerView() {
        adapter = new FavoriteAdapter(new ArrayList<>(), this);
        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {
        if (getContext() == null) return;
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Room> allRooms = dbHelper.getAllRooms();
        List<Hotel> favoriteHotels = new ArrayList<>();

        String[] beautifulImages = {
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=600&q=80"
        };
        int imgIndex = 0;

        for (Room room : allRooms) {
            if (dbHelper.isFavorite(room.getId())) {
                String imgUrl = (room.getImageUrl() != null && !room.getImageUrl().isEmpty())
                        ? room.getImageUrl() : beautifulImages[imgIndex % beautifulImages.length];
                imgIndex++;

                String location = (room.getLocation() != null && !room.getLocation().isEmpty())
                        ? room.getLocation() : "Hà Nội, Việt Nam";

                Hotel h = new Hotel(
                        String.valueOf(room.getId()),
                        room.getName(),
                        location,
                        imgUrl,
                        (float) room.getRating(),
                        (int) room.getPrice(),
                        true,
                        Arrays.asList("WiFi", "Gym")
                );
                favoriteHotels.add(h);
            }
        }
        adapter.setFavoriteList(favoriteHotels);
    }

    @Override
    public void onHotelClick(Hotel hotel, View imageView) {
        Intent intent = new Intent(getActivity(), com.example.datphongks.ui.hotel.HotelDetailActivity.class);
        intent.putExtra("hotel_id", hotel.getId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
