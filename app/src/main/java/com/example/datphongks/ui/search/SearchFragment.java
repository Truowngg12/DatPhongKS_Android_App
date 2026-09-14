package com.example.datphongks.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.databinding.FragmentSearchBinding;
import com.example.datphongks.ui.adapters.HotelAdapter;
import com.example.datphongks.ui.hotel.HotelDetailActivity;
import com.example.datphongks.viewmodels.SearchViewModel;

import java.util.List;

public class SearchFragment extends Fragment implements HotelAdapter.OnHotelClickListener {

    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private HotelAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.btnFilter.setOnClickListener(v -> {
            FilterBottomSheetFragment filterSheet = new FilterBottomSheetFragment();
            filterSheet.setOnFilterAppliedListener((minPrice, maxPrice, stars, amenities) -> {
                viewModel.setPriceRange(minPrice, maxPrice);
                viewModel.setStars(stars);
                viewModel.setAmenities(amenities);
            });
            filterSheet.show(getChildFragmentManager(), "filter");
        });
    }

    private void setupRecyclerView() {
        adapter = new HotelAdapter(this);
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSearchResults.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getFilteredHotels().observe(getViewLifecycleOwner(), hotels -> {
            if (hotels == null || hotels.isEmpty()) {
                binding.tvNoResults.setVisibility(View.VISIBLE);
                binding.rvSearchResults.setVisibility(View.GONE);
            } else {
                binding.tvNoResults.setVisibility(View.GONE);
                binding.rvSearchResults.setVisibility(View.VISIBLE);
                adapter.submitList(hotels);
            }
        });
    }

    @Override
    public void onHotelClick(Hotel hotel, View imageView) {
        Intent intent = new Intent(getActivity(), HotelDetailActivity.class);
        intent.putExtra("hotel_id", hotel.getId());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(), imageView, "hotel_image"
        );
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onFavoriteClick(Hotel hotel) {
        // SearchViewModel toggle favorite logic if needed
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}