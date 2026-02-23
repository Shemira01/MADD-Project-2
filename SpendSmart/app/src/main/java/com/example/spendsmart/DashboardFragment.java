package com.example.spendsmart;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Inflate the layout
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 2. Find both the ProgressBar and the TextView from XML
        ProgressBar budgetProgressBar = view.findViewById(R.id.budgetProgress);
        TextView remainingBalanceText = view.findViewById(R.id.tvRemainingBalance);

        // 3. Set up dummy data (Later, pull this from your database)
        double currentSpent = 950.0;
        double budgetLimit = 1000.0; 

        // 4. Call your math utility
        int progressPercentage = BudgetMathUtils.calculateSpendingPercentage(currentSpent, budgetLimit);
        double remainingBalance = budgetLimit - currentSpent;

        // 5. Apply the calculated percentage and handle visual insights
        if (budgetProgressBar != null) {
            budgetProgressBar.setProgress(progressPercentage);

            // Using an if statement to turn the bar Red if usage exceeds 90%
            if (progressPercentage >= 90) {
                budgetProgressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
            } else {
                // Default color (e.g., green or the theme's primary color)
                budgetProgressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN)); 
            }
        }

        if (remainingBalanceText != null) {
            remainingBalanceText.setText(String.format("Remaining Balance: $%.2f", remainingBalance));
        }

        return view;
    }
}
