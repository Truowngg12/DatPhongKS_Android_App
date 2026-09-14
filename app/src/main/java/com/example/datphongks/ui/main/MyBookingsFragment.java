package com.example.datphongks.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datphongks.data.repository.dummy.DummyBookingRepository;
import com.example.datphongks.databinding.FragmentMyBookingsBinding;
import com.example.datphongks.ui.adapters.BookingAdapter;
import com.google.android.material.tabs.TabLayout;

public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private BookingAdapter adapter;
    private DummyBookingRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new DummyBookingRepository();

        setupRecyclerView();
        setupTabs();

        loadBookings("Sắp tới");
    }

    private void setupRecyclerView() {
        // Đã sửa lỗi: Truyền tham số OnBookingClickListener (dưới dạng lambda) vào constructor
        adapter = new BookingAdapter(booking -> {
            Intent intent = new Intent(getActivity(), com.example.datphongks.ui.hotel.HotelDetailActivity.class);
            intent.putExtra("hotel_id", booking.getHotel().getId());
            startActivity(intent);
        });

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBookings.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    loadBookings(tab.getText().toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadBookings(String status) {
        String queryStatus = status;
        if ("Sắp tới".equalsIgnoreCase(status)) queryStatus = "Upcoming";
        else if ("Đã hoàn thành".equalsIgnoreCase(status)) queryStatus = "Completed";
        else if ("Đã hủy".equalsIgnoreCase(status)) queryStatus = "Cancelled";
        adapter.submitList(repository.getBookingsByStatus(queryStatus));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}