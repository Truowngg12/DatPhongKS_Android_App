package com.example.datphongks.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datphongks.R;
import com.example.datphongks.data.models.OnboardingItem;
import com.example.datphongks.databinding.ActivityOnboardingBinding;
import com.example.datphongks.ui.adapters.OnboardingAdapter;
import com.example.datphongks.ui.auth.AuthActivity;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupOnboarding();

        binding.btnNext.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
            } else {
                navigateToAuth();
            }
        });

        binding.btnSkip.setOnClickListener(v -> navigateToAuth());

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    binding.btnNext.setText(R.string.btn_get_started);
                } else {
                    binding.btnNext.setText(R.string.btn_next);
                }
            }
        });
    }

    private void setupOnboarding() {
        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1),
                R.drawable.ic_onboarding_1
        ));
        items.add(new OnboardingItem(
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2),
                R.drawable.ic_onboarding_2
        ));
        items.add(new OnboardingItem(
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3),
                R.drawable.ic_onboarding_3
        ));

        adapter = new OnboardingAdapter(items);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {}).attach();
    }

    private void navigateToAuth() {
        startActivity(new Intent(OnboardingActivity.this, AuthActivity.class));
        finish();
    }
}