package com.example.spendsmart;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddExpenseFragment extends Fragment {

    private EditText etAmount;
    private Spinner spCategory;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        // Initialize views
        etAmount = view.findViewById(R.id.etAmount);
        spCategory = view.findViewById(R.id.spCategory);
        btnSave = view.findViewById(R.id.btnSave);

        // Set click listener
        btnSave.setOnClickListener(v -> saveExpense());

        return view;
    }

    private void saveExpense() {

        // Get amount safely
        String amountText = etAmount.getText() != null
                ? etAmount.getText().toString().trim()
                : "";

        // Validate amount
        if (TextUtils.isEmpty(amountText)) {
            etAmount.setError("Enter amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid number");
            return;
        }

        // Get spinner value safely
        Object selectedItem = spCategory.getSelectedItem();

        if (selectedItem == null) {
            Toast.makeText(getContext(),
                    "Please select a category",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String category = selectedItem.toString();

        // Object Mapping
        Expense expense = new Expense(amount, category);

        // Confirmation
        Toast.makeText(getContext(),
                "Saved: " + expense.getCategory() + " - " + expense.getAmount(),
                Toast.LENGTH_SHORT).show();

        // Reset UI
        etAmount.setText("");
        spCategory.setSelection(0);
    }
}