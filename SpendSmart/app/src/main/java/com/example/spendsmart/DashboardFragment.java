package com.example.spendsmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private SharedPreferences sharedPreferences;

    // UI Elements
    private ProgressBar budgetProgressBar;
    private TextView remainingBalanceText;
    private TextInputEditText etIncomeInput;
    private Button btnSaveIncome;
    private RecyclerView rvRecentTransactions;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("SpendSmartPrefs", Context.MODE_PRIVATE);

        // Map UI Elements
        budgetProgressBar = view.findViewById(R.id.budgetProgress);
        remainingBalanceText = view.findViewById(R.id.tvRemainingBalance);
        etIncomeInput = view.findViewById(R.id.etIncomeInput);
        btnSaveIncome = view.findViewById(R.id.btnSaveIncome);

        // IMPORTANT: Make sure this ID matches the RecyclerView in your fragment_dashboard.xml
        rvRecentTransactions = view.findViewById(R.id.recyclerRecentTransactions);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Setup Save Income Button
        if (btnSaveIncome != null) {
            btnSaveIncome.setOnClickListener(v -> saveIncome());
        }

        // Setup the RecyclerView for Transactions
        if (rvRecentTransactions != null) {
            rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new TransactionAdapter(new ArrayList<>());
            rvRecentTransactions.setAdapter(adapter);
        }

        // Observe Total Spending for the Progress Bar
        expenseViewModel.getTotalSpending().observe(getViewLifecycleOwner(), this::updateDashboardUI);

        // Observe ALL Expenses for the Recent Transactions List
        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null && adapter != null) {
                // Create a copy of the list and reverse it so the newest transactions appear at the top
                List<Expense> recentList = new ArrayList<>(expenses);
                Collections.reverse(recentList);
                adapter.updateData(recentList);
            }
        });

        return view;
    }

    private void saveIncome() {
        if (etIncomeInput != null && etIncomeInput.getText() != null) {
            String inputStr = etIncomeInput.getText().toString().trim();
            if (!inputStr.isEmpty()) {
                double newIncome = Double.parseDouble(inputStr);

                sharedPreferences.edit().putFloat("UserIncome", (float) newIncome).apply();

                Toast.makeText(getContext(), "Income saved!", Toast.LENGTH_SHORT).show();
                etIncomeInput.setText("");

                Double currentSpent = expenseViewModel.getTotalSpending().getValue();
                updateDashboardUI(currentSpent);
            }
        }
    }

    private void updateDashboardUI(Double totalSpent) {
        double currentSpent = (totalSpent != null) ? totalSpent : 0.0;
        double userIncome = sharedPreferences.getFloat("UserIncome", 0.0f);

        if (userIncome <= 0.0) {
            if (remainingBalanceText != null) remainingBalanceText.setText("Please set your income below.");
            if (budgetProgressBar != null) budgetProgressBar.setProgress(0);
            return;
        }

        int progressPercentage = BudgetMathUtils.calculateSpendingPercentage(currentSpent, userIncome);
        double remainingBalance = userIncome - currentSpent;

        if (budgetProgressBar != null) {
            budgetProgressBar.setProgress(progressPercentage);
            if (progressPercentage >= 90) {
                budgetProgressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
            } else {
                budgetProgressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            }
        }

        if (remainingBalanceText != null) {
            remainingBalanceText.setText(String.format("Remaining Balance: ksh %.2f", remainingBalance));
        }
    }

    // ==========================================
    // INNER ADAPTER FOR RECENT TRANSACTIONS
    // ==========================================

    public static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
        private List<Expense> transactionList;

        public TransactionAdapter(List<Expense> transactionList) {
            this.transactionList = transactionList;
        }

        public void updateData(List<Expense> newTransactions) {
            this.transactionList = newTransactions;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Expense expense = transactionList.get(position);

            // Set the real data
            if (holder.tvCategory != null) {
                holder.tvCategory.setText(expense.getCategory());
            }
            if (holder.tvAmount != null) {
                holder.tvAmount.setText(String.format("ksh %.2f", expense.getAmount()));
            }
        }

        @Override
        public int getItemCount() {
            // Optional: Limit this to 5 or 10 if you only want to show the "Recent" ones
            return transactionList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCategory;
            TextView tvAmount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // IMPORTANT: Make sure these IDs match your item_transaction.xml text views
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvAmount = itemView.findViewById(R.id.tvAmount);
            }
        }
    }
}
