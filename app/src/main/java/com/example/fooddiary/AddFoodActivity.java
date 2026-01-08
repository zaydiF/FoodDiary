package com.example.fooddiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;

public class AddFoodActivity extends AppCompatActivity {

    private EditText etFoodName, etWeight, etCalories, etProtein, etCarbs, etFat;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etFoodName = findViewById(R.id.etFoodName);
        etWeight = findViewById(R.id.etWeight);
        etCalories = findViewById(R.id.etCalories);
        etProtein = findViewById(R.id.etProtein);
        etCarbs = findViewById(R.id.etCarbs);
        etFat = findViewById(R.id.etFat);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFoodItem();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveFoodItem() {
        String name = etFoodName.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String caloriesStr = etCalories.getText().toString().trim();
        String proteinStr = etProtein.getText().toString().trim();
        String carbsStr = etCarbs.getText().toString().trim();
        String fatStr = etFat.getText().toString().trim();

        if (name.isEmpty()) {
            etFoodName.setError("Введите название продукта");
            return;
        }

        if (weightStr.isEmpty()) {
            etWeight.setError("Введите вес");
            return;
        }

        if (caloriesStr.isEmpty()) {
            etCalories.setError("Введите калории");
            return;
        }

        try {
            int weight = Integer.parseInt(weightStr);
            int calories = Integer.parseInt(caloriesStr);
            int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);
            int carbs = carbsStr.isEmpty() ? 0 : Integer.parseInt(carbsStr);
            int fat = fatStr.isEmpty() ? 0 : Integer.parseInt(fatStr);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("foodName", name);
            resultIntent.putExtra("foodWeight", weight);
            resultIntent.putExtra("foodCalories", calories);
            resultIntent.putExtra("foodProtein", protein);
            resultIntent.putExtra("foodCarbs", carbs);
            resultIntent.putExtra("foodFat", fat);

            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (NumberFormatException e) {
            etCalories.setError("Проверьте правильность чисел");
        }
    }
}