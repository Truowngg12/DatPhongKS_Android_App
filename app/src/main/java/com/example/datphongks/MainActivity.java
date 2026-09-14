package com.example.datphongks;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.datphongks.databinding.ActivityMainBinding;
import com.example.datphongks.ui.home.HomeFragment;
import com.example.datphongks.ui.main.FavoritesFragment;
import com.example.datphongks.ui.main.MyBookingsFragment;
import com.example.datphongks.ui.main.ProfileFragment;
import com.example.datphongks.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Default fragment
        switchFragment(new HomeFragment(), "home");

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(new HomeFragment(), "home");
                return true;
            } else if (itemId == R.id.nav_search) {
                switchFragment(new SearchFragment(), "search");
                return true;
            } else if (itemId == R.id.nav_bookings) {
                switchFragment(new MyBookingsFragment(), "bookings");
                return true;
            } else if (itemId == R.id.nav_favorites) {
                switchFragment(new FavoritesFragment(), "favorites");
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(new ProfileFragment(), "profile");
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment, tag)
                .commit();
    }
}