package com.example.datphongks.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.datphongks.databinding.FragmentFilterBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentFilterBottomSheetBinding binding;
    private OnFilterAppliedListener listener;

    public interface OnFilterAppliedListener {
        void onFilterApplied(float minPrice, float maxPrice, List<Integer> stars, List<String> amenities);
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.priceSlider.setLabelFormatter(value -> String.format(Locale.getDefault(), "%,.0f đ", value));

        binding.btnApplyFilters.setOnClickListener(v -> {
            if (listener != null) {
                List<Float> values = binding.priceSlider.getValues();
                float minPrice = values.get(0);
                float maxPrice = values.get(1);

                List<Integer> selectedStars = new ArrayList<>();
                for (int i = 0; i < binding.chipGroupStars.getChildCount(); i++) {
                    Chip chip = (Chip) binding.chipGroupStars.getChildAt(i);
                    if (chip.isChecked()) {
                        selectedStars.add(Integer.parseInt(chip.getTag().toString()));
                    }
                }

                List<String> selectedAmenities = new ArrayList<>();
                for (int i = 0; i < binding.chipGroupAmenities.getChildCount(); i++) {
                    Chip chip = (Chip) binding.chipGroupAmenities.getChildAt(i);
                    if (chip.isChecked()) {
                        selectedAmenities.add(chip.getText().toString());
                    }
                }

                listener.onFilterApplied(minPrice, maxPrice, selectedStars, selectedAmenities);
                dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}