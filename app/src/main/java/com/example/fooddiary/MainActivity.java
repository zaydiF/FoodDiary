package com.example.fooddiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private FoodAdapter foodAdapter;
    private TextView tvDate, tvCalories, tvProtein, tvCarbs, tvFat;
    private Button btnAddFood, btnStats, btnPrevDay, btnNextDay, btnSettings;
    private EditText etSearch;
    private ProgressBar progressCalories;
    private TextView tvCaloriesProgress, tvCaloriesPercent, tvCaloriesGoal;

    private DayData currentDayData;
    private int waterGoal = 4000;

    private TextView tvCurrentDate;
    private LinearLayout cardQuickAdd, cardWater;

    private Date currentDate;
    private int calorieGoal = 2000;
    private String selectedEmoji = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("🟢 Простая инициализация");

        initViews();
        setupRecyclerView();

        currentDate = new Date();
        currentDayData = new DayData(currentDate);

        updateDisplay();
    }

    private void initViews() {
        System.out.println("🟡 Начинаем initViews");

        // ОСНОВНЫЕ ЭЛЕМЕНТЫ
        tvDate = findViewById(R.id.tvDate);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        System.out.println("🟡 tvDate: " + (tvDate != null) + ", tvCurrentDate: " + (tvCurrentDate != null));

        // Статистика БЖУ
        tvProtein = findViewById(R.id.tvProtein);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvFat = findViewById(R.id.tvFat);
        System.out.println("🟡 БЖУ: " + (tvProtein != null) + ", " + (tvCarbs != null) + ", " + (tvFat != null));

        // Прогресс калорий
        progressCalories = findViewById(R.id.progressCalories);
        tvCaloriesProgress = findViewById(R.id.tvCaloriesProgress);
        tvCaloriesPercent = findViewById(R.id.tvCaloriesPercent);
        System.out.println("🟡 Прогресс: " + (progressCalories != null) + ", " + (tvCaloriesProgress != null) + ", " + (tvCaloriesPercent != null));

        // Кнопки
        btnAddFood = findViewById(R.id.btnAddFood);
        btnStats = findViewById(R.id.btnStats);
        btnPrevDay = findViewById(R.id.btnPrevDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        btnSettings = findViewById(R.id.btnSettings);
        System.out.println("🟡 Кнопки найдены");

        // Другие элементы
        etSearch = findViewById(R.id.etSearch);
        foodRecyclerView = findViewById(R.id.foodRecyclerView);

        // Новые карточки
        cardQuickAdd = findViewById(R.id.cardQuickAdd);
        cardWater = findViewById(R.id.cardWater);

        System.out.println("🟢 Все элементы инициализированы");

        // Обработчики для новых карточек
        cardQuickAdd.setOnClickListener(v -> {
            showQuickAddDialog();
        });

        cardWater.setOnClickListener(v -> {
            showWaterTrackerDialog();
        });

        // Остальные обработчики...
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeeklyStatsActivity.class);
            startActivity(intent);
        });

        btnAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddFoodActivity.class);
            startActivityForResult(intent, 1);
        });

        btnSettings.setOnClickListener(v -> {
            showCalorieGoalDialog();
        });

        btnPrevDay.setOnClickListener(v -> {
            showPreviousDay();
        });

        btnNextDay.setOnClickListener(v -> {
            showNextDay();
        });

        setupSearch();
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(new FoodAdapter.OnFoodItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteFoodItem(position);
            }
        });
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodRecyclerView.setAdapter(foodAdapter);
    }

    private void showPreviousDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        currentDate = calendar.getTime();
        loadDayData(currentDate);
    }

    private void showNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Date today = new Date();
        if (!calendar.getTime().after(today)) {
            currentDate = calendar.getTime();
            loadDayData(currentDate);
        } else {
            Toast.makeText(this, "Нельзя перейти в будущее", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDayData(Date date) {
        // Используем FoodDataManager для получения данных
        currentDayData = FoodDataManager.getInstance(getApplicationContext()).getDayData(date);

        if (currentDayData == null) {
            currentDayData = new DayData(date);
        }

        updateDisplay();
    }

    private void updateDisplay() {
        if (currentDayData != null) {
            // Обновляем даты
            tvDate.setText(currentDayData.getDayName() + ", " + currentDayData.getDisplayDate());
            tvCurrentDate.setText(currentDayData.getDisplayDate());

            // ОБНОВЛЯЕМ СТАТИСТИКУ БЖУ
            tvProtein.setText(currentDayData.getTotalProtein() + "г");
            tvCarbs.setText(currentDayData.getTotalCarbs() + "г");
            tvFat.setText(currentDayData.getTotalFat() + "г");

            // ОБНОВЛЯЕМ ПРОГРЕСС-БАР КАЛОРИЙ
            updateCalorieProgress();

            // Обновляем список продуктов
            foodAdapter.setFoodItems(currentDayData.getFoodItems());

            // Очищаем поиск
            etSearch.setText("");
        }
    }

    private void updateCalorieProgress() {
        System.out.println("🟡 Вызов updateCalorieProgress");

        if (currentDayData == null) {
            System.out.println("🔴 currentDayData is null");
            return;
        }

        // Используем сохраненную цель калорий
        int currentCalories = currentDayData.getTotalCalories();

        System.out.println("🟡 Калории: " + currentCalories + " / " + calorieGoal);

        // Рассчитываем прогресс (не более 100%)
        int progress = calorieGoal > 0 ? (int) ((currentCalories / (float) calorieGoal) * 100) : 0;
        progress = Math.min(progress, 100);

        System.out.println("🟡 Прогресс: " + progress + "%");

        // Обновляем прогресс-бар с проверкой
        if (progressCalories != null) {
            progressCalories.setProgress(progress);
        } else {
            System.out.println("🔴 progressCalories is null");
        }

        // Обновляем тексты с проверками
        if (tvCaloriesProgress != null) {
            tvCaloriesProgress.setText(currentCalories + " / " + calorieGoal + " ккал");
        } else {
            System.out.println("🔴 tvCaloriesProgress is null");
        }

        if (tvCaloriesPercent != null) {
            tvCaloriesPercent.setText(progress + "%");
        } else {
            System.out.println("🔴 tvCaloriesPercent is null");
        }

        // Меняем цвет в зависимости от прогресса
        if (progressCalories != null) {
            if (progress >= 100) {
                progressCalories.setProgressTintList(ColorStateList.valueOf(Color.RED));
                if (tvCaloriesPercent != null) tvCaloriesPercent.setTextColor(Color.RED);
            } else if (progress >= 80) {
                progressCalories.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#F59E0B"))); // Оранжевый
                if (tvCaloriesPercent != null) tvCaloriesPercent.setTextColor(Color.parseColor("#F59E0B"));
            } else {
                progressCalories.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#10B981"))); // Зеленый
                if (tvCaloriesPercent != null) tvCaloriesPercent.setTextColor(Color.parseColor("#10B981"));
            }
        }

        System.out.println("🟢 updateCalorieProgress завершен");
    }

    private void loadCalorieGoal() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        calorieGoal = prefs.getInt("calorie_goal", 2000);
    }

    private void saveCalorieGoal(int goal) {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("calorie_goal", goal);
        editor.apply();
        calorieGoal = goal; // Обновляем переменную класса
        System.out.println("🟢 Норма калорий сохранена: " + goal);
    }

    private void showCalorieGoalDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_calorie_goal, null);
        builder.setView(dialogView);

        final EditText etCalorieGoal = dialogView.findViewById(R.id.etCalorieGoal);
        Button btnQuick1500 = dialogView.findViewById(R.id.btnQuick1500);
        Button btnQuick2000 = dialogView.findViewById(R.id.btnQuick2000);
        Button btnQuick2500 = dialogView.findViewById(R.id.btnQuick2500);

        etCalorieGoal.setText(String.valueOf(calorieGoal));

        // Изначально выделяем кнопку, соответствующую текущему значению
        updateButtonSelection(btnQuick1500, btnQuick2000, btnQuick2500, calorieGoal);

        btnQuick1500.setOnClickListener(v -> {
            etCalorieGoal.setText("1500");
            updateButtonSelection(btnQuick1500, btnQuick2000, btnQuick2500, 1500);
        });

        btnQuick2000.setOnClickListener(v -> {
            etCalorieGoal.setText("2000");
            updateButtonSelection(btnQuick1500, btnQuick2000, btnQuick2500, 2000);
        });

        btnQuick2500.setOnClickListener(v -> {
            etCalorieGoal.setText("3000");
            updateButtonSelection(btnQuick1500, btnQuick2000, btnQuick2500, 3000);
        });

        // Сохраняем ссылку на EditText в final переменную для использования в обработчике
        final EditText finalEtCalorieGoal = etCalorieGoal;

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String goalStr = finalEtCalorieGoal.getText().toString();
            if (!goalStr.isEmpty()) {
                try {
                    int newGoal = Integer.parseInt(goalStr);
                    if (newGoal >= 500 && newGoal <= 8000) {
                        saveCalorieGoal(newGoal);
                        updateCalorieProgress();
                        Toast.makeText(MainActivity.this, "Норма сохранена: " + newGoal + " ккал", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Введите значение от 500 до 8000 ккал", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Введите корректное число", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", null);

        // Создаем и показываем диалог
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Меняем цвет кнопок ПОСЛЕ показа диалога
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#10B981")); // Зеленый
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6B7280")); // Серый
    }

    // Метод для обновления выделения кнопок
    private void updateButtonSelection(Button btn1500, Button btn2000, Button btn3000, int selectedValue) {
        // Сбрасываем все кнопки к неактивному состоянию
        btn1500.setBackgroundColor(Color.parseColor("#F3F4F6")); // Серый фон
        btn1500.setTextColor(Color.parseColor("#6B7280")); // Серый текст

        btn2000.setBackgroundColor(Color.parseColor("#F3F4F6")); // Серый фон
        btn2000.setTextColor(Color.parseColor("#6B7280")); // Серый текст

        btn3000.setBackgroundColor(Color.parseColor("#F3F4F6")); // Серый фон
        btn3000.setTextColor(Color.parseColor("#6B7280")); // Серый текст

        // Выделяем активную кнопку
        switch (selectedValue) {
            case 1500:
                btn1500.setBackgroundColor(Color.parseColor("#10B981")); // Зеленый фон
                btn1500.setTextColor(Color.WHITE); // Белый текст
                break;
            case 2000:
                btn2000.setBackgroundColor(Color.parseColor("#10B981")); // Зеленый фон
                btn2000.setTextColor(Color.WHITE); // Белый текст
                break;
            case 3000:
                btn3000.setBackgroundColor(Color.parseColor("#10B981")); // Зеленый фон
                btn3000.setTextColor(Color.WHITE); // Белый текст
                break;
        }
    }

    // Метод для получения количества воды для конкретной даты
    private int getCurrentWaterForDate(Date date) {
        SharedPreferences prefs = getSharedPreferences("water_tracker", MODE_PRIVATE);
        String dateKey = DateUtils.getDateKey(date);
        return prefs.getInt("water_" + dateKey, 0);
    }

    // Метод для сохранения прогресса воды для конкретной даты
    private void saveWaterProgressForDate(Date date, int water) {
        SharedPreferences prefs = getSharedPreferences("water_tracker", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String dateKey = DateUtils.getDateKey(date);
        editor.putInt("water_" + dateKey, water);
        editor.apply();

        System.out.println("💧 Вода сохранена для даты " + dateKey + ": " + water + " мл");
    }

    // Метод для обновления отображения воды
    private void updateWaterDisplay(TextView tvWater, ProgressBar progressBar, int currentWater, int goal) {
        String waterText = currentWater + " / " + goal + " мл";
        tvWater.setText(waterText);

        int progress = goal > 0 ? (int) ((currentWater / (float) goal) * 100) : 0;
        progress = Math.min(progress, 100);
        progressBar.setProgress(progress);

        // Меняем цвет текста и прогресса в зависимости от заполнения
        if (currentWater >= goal) {
            // Если достигли или превысили лимит - красный цвет
            tvWater.setTextColor(Color.parseColor("#EF4444"));
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#EF4444"))); // Красный
        } else if (progress >= 80) {
            // Близко к лимиту - оранжевый
            tvWater.setTextColor(Color.parseColor("#F59E0B"));
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#F59E0B"))); // Оранжевый
        } else if (progress >= 50) {
            // Средний уровень - синий
            tvWater.setTextColor(Color.parseColor("#3B82F6"));
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#3B82F6"))); // Синий
        } else {
            // Низкий уровень - голубой
            tvWater.setTextColor(Color.parseColor("#60A5FA"));
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#60A5FA"))); // Голубой
        }
    }

    // БЫСТРОЕ ДОБАВЛЕНИЕ ПРОДУКТОВ
    private void showQuickAddDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        // Создаем кастомный layout для диалога
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quick_add, null);
        builder.setView(dialogView);

        // Находим элементы
        RecyclerView recyclerQuickFoods = dialogView.findViewById(R.id.recyclerQuickFoods);
        Button btnAddCustom = dialogView.findViewById(R.id.btnAddCustom);

        // Настраиваем RecyclerView для быстрых продуктов
        setupQuickFoodsRecycler(recyclerQuickFoods);

        // Кнопка добавления своего продукта
        btnAddCustom.setOnClickListener(v -> {
            showAddCustomQuickFoodDialog();
        });

        builder.setPositiveButton("Закрыть", (dialog, which) -> {
            // Просто закрываем диалог
        });

        // Создаем и показываем диалог
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Меняем цвет кнопки закрыть
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#10B981"));
    }

    private void setupQuickFoodsRecycler(RecyclerView recyclerView) {
        List<QuickFoodItem> quickFoods = getQuickFoods();
        QuickFoodAdapter adapter = new QuickFoodAdapter(quickFoods, new QuickFoodAdapter.OnQuickFoodClickListener() {
            @Override
            public void onAddClick(QuickFoodItem food) {
                addQuickFood(food);
                Toast.makeText(MainActivity.this, food.getName() + " добавлен!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(QuickFoodItem food) {
                deleteQuickFood(food);
            }

            @Override
            public void onEditClick(QuickFoodItem food) {
                showEditQuickFoodDialog(food);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void showAddCustomQuickFoodDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_quick_food, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etFoodName);
        EditText etWeight = dialogView.findViewById(R.id.etWeight);
        EditText etCalories = dialogView.findViewById(R.id.etCalories);
        EditText etProtein = dialogView.findViewById(R.id.etProtein);
        EditText etCarbs = dialogView.findViewById(R.id.etCarbs);
        EditText etFat = dialogView.findViewById(R.id.etFat);
        RecyclerView recyclerEmojis = dialogView.findViewById(R.id.recyclerEmojis);

        // Настраиваем выбор эмодзи
        setupEmojiRecycler(recyclerEmojis, "➕");

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String name = etName.getText().toString();
            String weightStr = etWeight.getText().toString();
            String caloriesStr = etCalories.getText().toString();
            String proteinStr = etProtein.getText().toString();
            String carbsStr = etCarbs.getText().toString();
            String fatStr = etFat.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название продукта", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int weight = weightStr.isEmpty() ? 100 : Integer.parseInt(weightStr);
                int calories = caloriesStr.isEmpty() ? 0 : Integer.parseInt(caloriesStr);
                int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);
                int carbs = carbsStr.isEmpty() ? 0 : Integer.parseInt(carbsStr);
                int fat = fatStr.isEmpty() ? 0 : Integer.parseInt(fatStr);

                // Получаем выбранный эмодзи
                String selectedEmoji = getSelectedEmoji();
                if (selectedEmoji.isEmpty()) {
                    selectedEmoji = "➕";
                }

                QuickFoodItem newFood = new QuickFoodItem(name, weight, calories, protein, carbs, fat, selectedEmoji);
                addCustomQuickFood(newFood);
                Toast.makeText(this, "Продукт добавлен в быстрые!", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Проверьте правильность чисел", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#10B981"));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6B7280"));
    }

    private void showEditQuickFoodDialog(QuickFoodItem food) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_quick_food, null);
        builder.setView(dialogView);

        // Находим заголовок и меняем его
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText("✏️ Редактировать продукт");
        }

        EditText etName = dialogView.findViewById(R.id.etFoodName);
        EditText etWeight = dialogView.findViewById(R.id.etWeight);
        EditText etCalories = dialogView.findViewById(R.id.etCalories);
        EditText etProtein = dialogView.findViewById(R.id.etProtein);
        EditText etCarbs = dialogView.findViewById(R.id.etCarbs);
        EditText etFat = dialogView.findViewById(R.id.etFat);
        RecyclerView recyclerEmojis = dialogView.findViewById(R.id.recyclerEmojis);

        // Заполняем текущие значения
        etName.setText(food.getName());
        etWeight.setText(String.valueOf(food.getWeight()));
        etCalories.setText(String.valueOf(food.getCalories()));
        etProtein.setText(String.valueOf(food.getProtein()));
        etCarbs.setText(String.valueOf(food.getCarbs()));
        etFat.setText(String.valueOf(food.getFat()));

        // Настраиваем выбор эмодзи
        setupEmojiRecycler(recyclerEmojis, food.getEmoji());

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String name = etName.getText().toString();
            String weightStr = etWeight.getText().toString();
            String caloriesStr = etCalories.getText().toString();
            String proteinStr = etProtein.getText().toString();
            String carbsStr = etCarbs.getText().toString();
            String fatStr = etFat.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название продукта", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int weight = weightStr.isEmpty() ? 100 : Integer.parseInt(weightStr);
                int calories = caloriesStr.isEmpty() ? 0 : Integer.parseInt(caloriesStr);
                int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);
                int carbs = carbsStr.isEmpty() ? 0 : Integer.parseInt(carbsStr);
                int fat = fatStr.isEmpty() ? 0 : Integer.parseInt(fatStr);

                // Получаем выбранный эмодзи
                String selectedEmoji = getSelectedEmoji();
                if (selectedEmoji.isEmpty()) {
                    selectedEmoji = food.getEmoji();
                }

                QuickFoodItem updatedFood = new QuickFoodItem(food.getId(), name, weight, calories, protein, carbs, fat, selectedEmoji);
                updateQuickFood(updatedFood);
                Toast.makeText(this, "Продукт обновлен!", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Проверьте правильность чисел", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#10B981"));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6B7280"));
    }

    private void setupEmojiRecycler(RecyclerView recyclerView, String currentEmoji) {
        List<String> emojis = Arrays.asList(
                "🥣", "🍗", "🍚", "🍎", "🥚", "🥛", "🥦", "🍞", "🧀", "🥩",
                "🍣", "🍕", "🍔", "🌮", "🥗", "🍜", "🍲", "🥘", "🍛", "🍤",
                "🥓", "🍳", "🥑", "🍌", "🍇", "🍓", "🍊", "🍉", "🍐", "🥭",
                "🍍", "🥥", "🥝", "🍅", "🥒", "🥕", "🌽", "🍠", "🥔", "🧅",
                "🍄", "🥜", "🌰", "🍪", "🍩", "🍰", "🎂", "🧁", "🍫", "🍬"
        );

        selectedEmoji = currentEmoji; // Устанавливаем текущий эмодзи

        EmojiAdapter adapter = new EmojiAdapter(emojis, selectedEmoji, new EmojiAdapter.OnEmojiClickListener() {
            @Override
            public void onEmojiClick(String emoji) {
                selectedEmoji = emoji;
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 6));
        recyclerView.setAdapter(adapter);
    }

    private String getSelectedEmoji() {
        return selectedEmoji;
    }

    // Метод для добавления быстрого продукта в дневник
    private void addQuickFood(QuickFoodItem quickFood) {
        FoodItem food = new FoodItem(
                quickFood.getName(),
                quickFood.getWeight(),
                quickFood.getCalories(),
                quickFood.getProtein(),
                quickFood.getCarbs(),
                quickFood.getFat(),
                null,
                currentDate
        );
        addFoodItem(food);
    }

    // Метод для добавления кастомного продукта в список быстрых
    private void addCustomQuickFood(QuickFoodItem food) {
        List<QuickFoodItem> quickFoods = getQuickFoods();
        quickFoods.add(food);
        saveAllQuickFoods(quickFoods);
    }

    // Метод для удаления любого продукта (и стандартного и кастомного)
    private void deleteQuickFood(QuickFoodItem food) {
        List<QuickFoodItem> quickFoods = getQuickFoods();

        // Удаляем из списка
        quickFoods.removeIf(item -> item.getId().equals(food.getId()));

        // Сохраняем обновленный список
        saveAllQuickFoods(quickFoods);
        Toast.makeText(this, "Продукт удален из быстрых", Toast.LENGTH_SHORT).show();
    }

    // Метод для обновления продукта
    private void updateQuickFood(QuickFoodItem updatedFood) {
        List<QuickFoodItem> quickFoods = getQuickFoods();

        // Находим и обновляем продукт
        for (int i = 0; i < quickFoods.size(); i++) {
            if (quickFoods.get(i).getId().equals(updatedFood.getId())) {
                quickFoods.set(i, updatedFood);
                break;
            }
        }

        saveAllQuickFoods(quickFoods);
    }

    // Получение списка всех быстрых продуктов
    private List<QuickFoodItem> getQuickFoods() {
        List<QuickFoodItem> quickFoods = new ArrayList<>();

        // Загружаем все продукты (стандартные + кастомные)
        SharedPreferences prefs = getSharedPreferences("quick_foods", MODE_PRIVATE);
        String allFoodsJson = prefs.getString("all_foods", "");

        if (!allFoodsJson.isEmpty()) {
            // Загружаем из сохраненных
            try {
                JSONArray jsonArray = new JSONArray(allFoodsJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    QuickFoodItem food = new QuickFoodItem(
                            json.getString("id"),
                            json.getString("name"),
                            json.getInt("weight"),
                            json.getInt("calories"),
                            json.getInt("protein"),
                            json.getInt("carbs"),
                            json.getInt("fat"),
                            json.getString("emoji")
                    );
                    quickFoods.add(food);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Первый запуск - создаем стандартные продукты
            quickFoods.add(new QuickFoodItem("Овсяная каша", 100, 88, 3, 15, 2, "🥣"));
            quickFoods.add(new QuickFoodItem("Куриная грудка", 150, 165, 31, 0, 4, "🍗"));
            quickFoods.add(new QuickFoodItem("Рис отварной", 150, 195, 4, 45, 1, "🍚"));
            quickFoods.add(new QuickFoodItem("Яблоко", 150, 78, 0, 20, 0, "🍎"));
            quickFoods.add(new QuickFoodItem("Яйца вареные", 100, 155, 13, 1, 11, "🥚"));
            quickFoods.add(new QuickFoodItem("Греческий йогурт", 100, 59, 10, 4, 0, "🥛"));

            // Сохраняем стандартные продукты
            saveAllQuickFoods(quickFoods);
        }

        return quickFoods;
    }

    // Сохранение всех продуктов (и стандартных и кастомных)
    private void saveAllQuickFoods(List<QuickFoodItem> quickFoods) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (QuickFoodItem food : quickFoods) {
                JSONObject json = new JSONObject();
                json.put("id", food.getId());
                json.put("name", food.getName());
                json.put("weight", food.getWeight());
                json.put("calories", food.getCalories());
                json.put("protein", food.getProtein());
                json.put("carbs", food.getCarbs());
                json.put("fat", food.getFat());
                json.put("emoji", food.getEmoji());
                jsonArray.put(json);
            }

            SharedPreferences prefs = getSharedPreferences("quick_foods", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("all_foods", jsonArray.toString());
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFoodItem(FoodItem foodItem) {
        // Сохраняем через FoodDataManager с контекстом
        FoodDataManager.getInstance(getApplicationContext()).addFoodItem(foodItem);

        // Перезагружаем данные
        loadDayData(currentDate);
    }

    private void deleteFoodItem(int position) {
        if (currentDayData != null && position >= 0 && position < currentDayData.getFoodItemsCount()) {
            FoodItem item = currentDayData.getFoodItems().get(position);
            FoodDataManager.getInstance(getApplicationContext()).removeFoodItem(item);
            loadDayData(currentDate);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("foodName");
            int weight = data.getIntExtra("foodWeight", 0);
            int calories = data.getIntExtra("foodCalories", 0);
            int protein = data.getIntExtra("foodProtein", 0);
            int carbs = data.getIntExtra("foodCarbs", 0);
            int fat = data.getIntExtra("foodFat", 0);
            String imagePath = data.getStringExtra("foodImagePath");

            FoodItem newFood = new FoodItem(name, weight, calories, protein, carbs, fat, imagePath, currentDate);
            addFoodItem(newFood);
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoodItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterFoodItems(String query) {
        if (currentDayData == null) return;

        List<FoodItem> filteredList;

        if (query.isEmpty()) {
            filteredList = currentDayData.getFoodItems();
        } else {
            filteredList = new ArrayList<>();
            for (FoodItem item : currentDayData.getFoodItems()) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        foodAdapter.setFoodItems(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDayData(currentDate);
        loadCalorieGoal();
        updateCalorieProgress();
    }

    // ТРЕКЕР ВОДЫ С НАСТРОЙКАМИ
    private void showWaterTrackerDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_water_tracker, null);
        builder.setView(dialogView);

        TextView tvCurrentWater = dialogView.findViewById(R.id.tvCurrentWater);
        ProgressBar progressWater = dialogView.findViewById(R.id.progressWater);
        Button btnAdd250ml = dialogView.findViewById(R.id.btnAdd250ml);
        Button btnAdd500ml = dialogView.findViewById(R.id.btnAdd500ml);
        Button btnReset = dialogView.findViewById(R.id.btnReset);
        Button btnSettings = dialogView.findViewById(R.id.btnWaterSettings);

        loadWaterSettings();

        final int[] currentWater = {getCurrentWaterForDate(currentDate)};

        updateWaterDisplay(tvCurrentWater, progressWater, currentWater[0], waterGoal);

        btnAdd250ml.setOnClickListener(v -> {
            if (currentWater[0] < waterGoal) {
                currentWater[0] += 250;
                if (currentWater[0] > waterGoal) {
                    currentWater[0] = waterGoal;
                }
                saveWaterProgressForDate(currentDate, currentWater[0]);
                updateWaterDisplay(tvCurrentWater, progressWater, currentWater[0], waterGoal);
                updateWaterButtonSelection(btnAdd250ml, btnAdd500ml, 250);
            }
        });

        btnAdd500ml.setOnClickListener(v -> {
            if (currentWater[0] < waterGoal) {
                currentWater[0] += 500;
                if (currentWater[0] > waterGoal) {
                    currentWater[0] = waterGoal;
                }
                saveWaterProgressForDate(currentDate, currentWater[0]);
                updateWaterDisplay(tvCurrentWater, progressWater, currentWater[0], waterGoal);
                updateWaterButtonSelection(btnAdd250ml, btnAdd500ml, 500);
            }
        });

        btnReset.setOnClickListener(v -> {
            currentWater[0] = 0;
            saveWaterProgressForDate(currentDate, currentWater[0]);
            updateWaterDisplay(tvCurrentWater, progressWater, currentWater[0], waterGoal);
            updateWaterButtonSelection(btnAdd250ml, btnAdd500ml, 0);
        });

        btnSettings.setOnClickListener(v -> {
            showWaterSettingsDialog();
        });

        // Изначально никакая кнопка не выделена
        updateWaterButtonSelection(btnAdd250ml, btnAdd500ml, 0);

        builder.setPositiveButton("Закрыть", (dialog, which) -> {});

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#10B981"));
    }

    private void showWaterSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_water_settings, null);
        builder.setView(dialogView);

        final EditText etWaterGoal = dialogView.findViewById(R.id.etWaterGoal);
        Button btnQuick1500 = dialogView.findViewById(R.id.btnQuick1500);
        Button btnQuick2000 = dialogView.findViewById(R.id.btnQuick2000);
        Button btnQuick3000 = dialogView.findViewById(R.id.btnQuick3000);
        Button btnQuick4000 = dialogView.findViewById(R.id.btnQuick4000);

        etWaterGoal.setText(String.valueOf(waterGoal));

        // Изначально выделяем кнопку, соответствующую текущему значению
        updateWaterGoalButtonSelection(btnQuick1500, btnQuick2000, btnQuick3000, btnQuick4000, waterGoal);

        btnQuick1500.setOnClickListener(v -> {
            etWaterGoal.setText("1500");
            updateWaterGoalButtonSelection(btnQuick1500, btnQuick2000, btnQuick3000, btnQuick4000, 1500);
        });

        btnQuick2000.setOnClickListener(v -> {
            etWaterGoal.setText("2000");
            updateWaterGoalButtonSelection(btnQuick1500, btnQuick2000, btnQuick3000, btnQuick4000, 2000);
        });

        btnQuick3000.setOnClickListener(v -> {
            etWaterGoal.setText("3000");
            updateWaterGoalButtonSelection(btnQuick1500, btnQuick2000, btnQuick3000, btnQuick4000, 3000);
        });

        btnQuick4000.setOnClickListener(v -> {
            etWaterGoal.setText("4000");
            updateWaterGoalButtonSelection(btnQuick1500, btnQuick2000, btnQuick3000, btnQuick4000, 4000);
        });

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String goalStr = etWaterGoal.getText().toString();
            if (!goalStr.isEmpty()) {
                try {
                    int newGoal = Integer.parseInt(goalStr);
                    if (newGoal >= 500 && newGoal <= 10000) {
                        saveWaterGoal(newGoal);
                        Toast.makeText(MainActivity.this, "Лимит воды сохранен: " + newGoal + " мл", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Введите значение от 500 до 10000 мл", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Введите корректное число", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", null);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#10B981"));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6B7280"));
    }

    private void loadWaterSettings() {
        SharedPreferences prefs = getSharedPreferences("water_tracker", MODE_PRIVATE);
        waterGoal = prefs.getInt("water_goal", 4000);
    }

    private void saveWaterGoal(int goal) {
        SharedPreferences prefs = getSharedPreferences("water_tracker", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("water_goal", goal);
        editor.apply();
        waterGoal = goal;
    }
    // Метод для выделения кнопок добавления воды
    private void updateWaterButtonSelection(Button btn250ml, Button btn500ml, int selectedSize) {
        // Сбрасываем все кнопки
        btn250ml.setBackgroundColor(Color.parseColor("#F3F4F6"));
        btn250ml.setTextColor(Color.parseColor("#6B7280"));

        btn500ml.setBackgroundColor(Color.parseColor("#F3F4F6"));
        btn500ml.setTextColor(Color.parseColor("#6B7280"));

        // Выделяем активную кнопку
        switch (selectedSize) {
            case 250:
                btn250ml.setBackgroundColor(Color.parseColor("#3B82F6")); // Синий
                btn250ml.setTextColor(Color.WHITE);
                break;
            case 500:
                btn500ml.setBackgroundColor(Color.parseColor("#3B82F6")); // Синий
                btn500ml.setTextColor(Color.WHITE);
                break;
        }
    }

    // Метод для выделения кнопок лимита воды
    private void updateWaterGoalButtonSelection(Button btn1500, Button btn2000, Button btn3000, Button btn4000, int selectedValue) {
        // Сбрасываем все кнопки к неактивному состоянию
        btn1500.setBackgroundColor(Color.parseColor("#F3F4F6"));
        btn1500.setTextColor(Color.parseColor("#6B7280"));

        btn2000.setBackgroundColor(Color.parseColor("#F3F4F6"));
        btn2000.setTextColor(Color.parseColor("#6B7280"));

        btn3000.setBackgroundColor(Color.parseColor("#F3F4F6"));
        btn3000.setTextColor(Color.parseColor("#6B7280"));

        btn4000.setBackgroundColor(Color.parseColor("#F3F4F6"));
        btn4000.setTextColor(Color.parseColor("#6B7280"));

        // Выделяем активную кнопку
        switch (selectedValue) {
            case 1500:
                btn1500.setBackgroundColor(Color.parseColor("#3B82F6")); // Синий
                btn1500.setTextColor(Color.WHITE);
                break;
            case 2000:
                btn2000.setBackgroundColor(Color.parseColor("#3B82F6")); // Синий
                btn2000.setTextColor(Color.WHITE);
                break;
            case 3000:
                btn3000.setBackgroundColor(Color.parseColor("#3B82F6")); // Синий
                btn3000.setTextColor(Color.WHITE);
                break;
            case 4000:
                btn4000.setBackgroundColor(Color.parseColor("#3B82F6")); // Синий
                btn4000.setTextColor(Color.WHITE);
                break;
        }
    }

}