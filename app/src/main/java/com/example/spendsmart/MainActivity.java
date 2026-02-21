package com.example.spendsmart;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // FIX 1: Use the actual DashboardFragment class as the default
        loadFragment(new DashboardFragment());

        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            // FIX 2: Map menu IDs to their actual Fragment classes
            if (id == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (id == R.id.navigation_add) {
                selectedFragment = new AddExpenseFragment();
            } else if (id == R.id.navigation_insights) {
                selectedFragment = new InsightsFragment();
            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}