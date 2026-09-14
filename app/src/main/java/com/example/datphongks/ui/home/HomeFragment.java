package com.example.datphongks.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datphongks.R;
import com.example.datphongks.data.models.Category;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.databinding.FragmentHomeBinding;
import com.example.datphongks.simplebooking.DatabaseHelper;
import com.example.datphongks.simplebooking.Room;
import com.example.datphongks.ui.adapters.CategoryAdapter;
import com.example.datphongks.ui.adapters.HotelAdapter;
import com.example.datphongks.ui.search.FilterBottomSheetFragment;
import com.example.datphongks.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements HotelAdapter.OnHotelClickListener {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HotelAdapter hotelAdapter;
    private CategoryAdapter categoryAdapter;
    private String currentFilter = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerViews();
        observeViewModel();
        setupSearchListeners(); // NỐI LẠI SỰ KIỆN CLICK
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromSQLite();
    }

    private void loadDataFromSQLite() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Room> rooms;

        switch (currentFilter) {
            case "HOT_DEALS":
                rooms = dbHelper.getHotDeals();
                break;
            case "TOP_RATED":
                rooms = dbHelper.getTopRated();
                break;
            default:
                rooms = dbHelper.getAllRooms();
                break;
        }
        mapAndDisplayRooms(rooms);
    }

    private void setupSearchListeners() {
        // 1. THANH TÌM KIẾM
        binding.cardSearch.setOnClickListener(v -> showSearchDialog());

        // 2. CHỌN NGÀY
        binding.btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(getContext(), "Ngày đã chọn: " + selectedDate, Toast.LENGTH_SHORT).show();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 3. CHỌN KHÁCH / PHÒNG
        binding.btnPickGuests.setOnClickListener(v -> showGuestSelectionDialog());

        // 4. MỞ DIALOG BỘ LỌC (Nhấn giữ thanh tìm kiếm)
        binding.cardSearch.setOnLongClickListener(v -> {
            openFilterBottomSheet();
            return true;
        });
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tìm kiếm phòng");

        final EditText input = new EditText(requireContext());
        input.setHint("Nhập tên phòng hoặc địa điểm...");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Tìm", (dialog, which) -> {
            String query = input.getText().toString().trim();
            performHomeSearch(query);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void openFilterBottomSheet() {
        FilterBottomSheetFragment filterSheet = new FilterBottomSheetFragment();
        filterSheet.setOnFilterAppliedListener((minPrice, maxPrice, stars, amenities) -> {
            int selectedStar = stars.isEmpty() ? 0 : stars.get(0);
            String selectedAmenity = amenities.isEmpty() ? "" : amenities.get(0);
            
            // 3. NỐI LOGIC APPLY FILTER
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            List<Room> filteredRooms = dbHelper.filterRooms(selectedStar, selectedAmenity, (int)minPrice, (int)maxPrice);
            mapAndDisplayRooms(filteredRooms);
            
            Toast.makeText(getContext(), "Đã tìm thấy " + filteredRooms.size() + " kết quả", Toast.LENGTH_SHORT).show();
        });
        filterSheet.show(getChildFragmentManager(), "filter_home");
    }

    private void showGuestSelectionDialog() {
        String[] options = {"1 Người, 1 Phòng", "2 Người, 1 Phòng", "3 Người, 2 Phòng", "4+ Người, 2 Phòng"};
        new AlertDialog.Builder(getContext())
                .setTitle("Chọn số lượng")
                .setItems(options, (dialog, which) -> {
                    Toast.makeText(getContext(), "Đã chọn: " + options[which], Toast.LENGTH_SHORT).show();
                    loadDataFromSQLite();
                })
                .show();
    }

    private void performHomeSearch(String query) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Room> rooms = dbHelper.searchRooms(query, 0, 5000000, 0);
        mapAndDisplayRooms(rooms);
    }

    private void applyAdvancedFilters(int star, String amenity, int min, int max) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Room> rooms = dbHelper.filterRooms(star, amenity, min, max);
        mapAndDisplayRooms(rooms);
        Toast.makeText(getContext(), "Đã lọc " + rooms.size() + " phòng", Toast.LENGTH_SHORT).show();
    }

    private void mapAndDisplayRooms(List<Room> rooms) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Hotel> mappedHotels = new ArrayList<>();
        String[] beautifulImages = {
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=600&q=80"
        };

        int imageIndex = 0;
        for (Room r : rooms) {
            String imageUrl = (r.getImageUrl() != null && !r.getImageUrl().isEmpty())
                    ? r.getImageUrl()
                    : beautifulImages[imageIndex % beautifulImages.length];
            imageIndex++;

            Hotel h = new Hotel(
                    String.valueOf(r.getId()),
                    r.getName(),
                    "Tiện ích: " + r.getAmenities(),
                    imageUrl,
                    (float) r.getRating(),
                    (int) r.getPrice(),
                    dbHelper.isFavorite(r.getId()),
                    Arrays.asList("WiFi", "Gym")
            );
            mappedHotels.add(h);
        }
        // Yêu cầu 2: BẮT BUỘC tạo ArrayList mới trước khi submitList để DiffUtil nhận biết sự thay đổi
        hotelAdapter.submitList(new ArrayList<>(mappedHotels));
    }

    private void setupRecyclerViews() {
        // Hotel list
        hotelAdapter = new HotelAdapter(this);
        binding.rvHotels.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHotels.setAdapter(hotelAdapter);

        // Categories list
        categoryAdapter = new CategoryAdapter(category -> {
            // Update selection UI
            List<Category> updatedList = new ArrayList<>();
            for (Category c : categoryAdapter.getCurrentList()) {
                updatedList.add(new Category(c.getId(), c.getName(), c.getId().equals(category.getId())));
            }
            categoryAdapter.submitList(updatedList);

            // Apply filter logic
            if (category.getName().contains("Hot") || category.getName().contains("Ưu đãi")) currentFilter = "HOT_DEALS";
            else if (category.getName().contains("Top") || category.getName().contains("Đánh giá")) currentFilter = "TOP_RATED";
            else currentFilter = "ALL";
            
            loadDataFromSQLite();
        });
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategories.setAdapter(categoryAdapter);

        // Load initial categories
        categoryAdapter.submitList(Arrays.asList(
                new Category("1", "Tất cả khách sạn", true),
                new Category("2", "Ưu đãi hot", false),
                new Category("3", "Đánh giá cao", false),
                new Category("4", "Phổ biến", false)
        ));
    }

    private void observeViewModel() {
    }

    @Override
    public void onHotelClick(Hotel hotel, View imageView) {
        Intent intent = new Intent(getActivity(), com.example.datphongks.ui.hotel.HotelDetailActivity.class);
        intent.putExtra("hotel_id", hotel.getId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Hotel hotel) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.toggleFavorite(Integer.parseInt(hotel.getId()));
        
        // RE-LOAD data to update UI heart icon
        loadDataFromSQLite();
        
        Toast.makeText(getContext(), !hotel.isFavorite() ? "Đã thêm vào Yêu thích ❤️" : "Đã xóa khỏi Yêu thích", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}