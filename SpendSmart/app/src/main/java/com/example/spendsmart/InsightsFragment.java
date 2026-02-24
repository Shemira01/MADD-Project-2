package com.example.spendsmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsightsFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private RecyclerView recyclerInsights;
    private TextView tvEmptyState;
    private CategoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insights, container, false);

        // 1. Map your views from the original XML
        recyclerInsights = view.findViewById(R.id.recyclerInsights);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        // 2. Setup the RecyclerView
        recyclerInsights.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(new ArrayList<>());
        recyclerInsights.setAdapter(adapter);

        // 3. Initialize ViewModel and observe data
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {

            // Handle empty state visibility
            if (expenses == null || expenses.isEmpty()) {
                recyclerInsights.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerInsights.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);

                // Process the data if the list is not empty
                processInsightsData(expenses);
            }
        });

        return view;
    }

    private void processInsightsData(List<Expense> expenses) {
        double totalSpent = 0.0;
        Map<String, Double> categoryTotals = new HashMap<>();

        // Aggregate expenses by category
        for (Expense exp : expenses) {
            totalSpent += exp.getAmount();
            categoryTotals.put(exp.getCategory(),
                    categoryTotals.getOrDefault(exp.getCategory(), 0.0) + exp.getAmount());
        }

        // Convert the Map into a List of Insight objects for the RecyclerView
        List<CategoryInsight> insightsList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            int percentage = (totalSpent > 0) ? (int) Math.round((amount / totalSpent) * 100) : 0;

            insightsList.add(new CategoryInsight(category, amount, percentage));
        }

        // Send the new data to the adapter
        adapter.updateData(insightsList);
    }

    // ==========================================
    // INNER CLASSES FOR RECYCLERVIEW COMPONENTS
    // ==========================================

    // 1. Data Model representing a single category's stats
    public static class CategoryInsight {
        String categoryName;
        double amount;
        int percentage;

        public CategoryInsight(String categoryName, double amount, int percentage) {
            this.categoryName = categoryName;
            this.amount = amount;
            this.percentage = percentage;
        }
    }

    // 2. Adapter to bind the Data Model to item_category.xml
    public static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        private List<CategoryInsight> insightsList;

        public CategoryAdapter(List<CategoryInsight> insightsList) {
            this.insightsList = insightsList;
        }

        public void updateData(List<CategoryInsight> newInsightsList) {
            this.insightsList = newInsightsList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CategoryInsight insight = insightsList.get(position);

            holder.tvCategoryName.setText(insight.categoryName);
            holder.tvPercentage.setText(String.format("ksh%.2f (%d%%)", insight.amount, insight.percentage));
            holder.progressCategory.setProgress(insight.percentage);
        }

        @Override
        public int getItemCount() {
            return insightsList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCategoryName;
            TextView tvPercentage;
            ProgressBar progressCategory;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                tvPercentage = itemView.findViewById(R.id.tvPercentage);
                progressCategory = itemView.findViewById(R.id.progressCategory);
            }
        }
    }
}
