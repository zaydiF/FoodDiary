package com.example.fooddiary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etAge, etHeight, etWeight;
    private Spinner spinnerGoal, spinnerActivity;
    private Button btnSave;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadProfile();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        spinnerActivity = findViewById(R.id.spinnerActivity);
        btnSave = findViewById(R.id.btnSave);
        tvResult = findViewById(R.id.tvResult);

        // Настройка спиннеров
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.goal_options, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);

        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_options, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(activityAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        String profileJson = prefs.getString("profile", "");

        if (!profileJson.isEmpty()) {
            Gson gson = new Gson();
            UserProfile profile = gson.fromJson(profileJson, UserProfile.class);

            etName.setText(profile.getName());
            etAge.setText(String.valueOf(profile.getAge()));
            etHeight.setText(String.valueOf(profile.getHeight()));
            etWeight.setText(String.valueOf(profile.getWeight()));

            // Устанавливаем спиннеры
            switch (profile.getGoal()) {
                case "lose": spinnerGoal.setSelection(0); break;
                case "maintain": spinnerGoal.setSelection(1); break;
                case "gain": spinnerGoal.setSelection(2); break;
            }

            spinnerActivity.setSelection(profile.getActivityLevel() - 1);

            updateResult(profile);
        }
    }

    private void saveProfile() {
        // Проверка введенных данных
        if (!validateInput()) return;

        String name = etName.getText().toString();
        int age = Integer.parseInt(etAge.getText().toString());
        int height = Integer.parseInt(etHeight.getText().toString());
        double weight = Double.parseDouble(etWeight.getText().toString());

        String goal = getGoalValue();
        int activityLevel = spinnerActivity.getSelectedItemPosition() + 1;

        UserProfile profile = new UserProfile(name, age, height, weight, goal, activityLevel);

        // Сохраняем в SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String profileJson = gson.toJson(profile);
        editor.putString("profile", profileJson);
        editor.apply();

        updateResult(profile);

        // Показываем сообщение об успехе
        tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        tvResult.setText("Профиль сохранен! Дневная норма: " + profile.getDailyCalorieGoal() + " ккал");
    }

    private String getGoalValue() {
        int position = spinnerGoal.getSelectedItemPosition();
        switch (position) {
            case 0: return "lose";
            case 1: return "maintain";
            case 2: return "gain";
            default: return "maintain";
        }
    }

    private boolean validateInput() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Введите имя");
            return false;
        }
        if (etAge.getText().toString().isEmpty() || Integer.parseInt(etAge.getText().toString()) < 1) {
            etAge.setError("Введите корректный возраст");
            return false;
        }
        if (etHeight.getText().toString().isEmpty() || Integer.parseInt(etHeight.getText().toString()) < 50) {
            etHeight.setError("Введите корректный рост");
            return false;
        }
        if (etWeight.getText().toString().isEmpty() || Double.parseDouble(etWeight.getText().toString()) < 10) {
            etWeight.setError("Введите корректный вес");
            return false;
        }
        return true;
    }

    private void updateResult(UserProfile profile) {
        String result = "Дневная норма калорий: " + profile.getDailyCalorieGoal() + " ккал\n" +
                "Цель: " + profile.getGoalDisplayName() + "\n" +
                "Активность: " + profile.getActivityDisplayName();
        tvResult.setText(result);
    }
}