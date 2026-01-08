package com.example.fooddiary;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeeklyStatsActivity extends AppCompatActivity {

    private TextView tvWeekRange, tvAverageCalories, tvTotalCalories;
    private TextView tvAverageProtein, tvAverageCarbs, tvAverageFat;
    private TextView tvBestDay, tvWorstDay;
    private LineChart chartCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_stats);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("FoodDiary");
        }

        initViews();
        showRealWeeklyStats();
    }

    private void initViews() {
        tvWeekRange = findViewById(R.id.tvWeekRange);
        tvAverageCalories = findViewById(R.id.tvAverageCalories);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvAverageProtein = findViewById(R.id.tvAverageProtein);
        tvAverageCarbs = findViewById(R.id.tvAverageCarbs);
        tvAverageFat = findViewById(R.id.tvAverageFat);
        tvBestDay = findViewById(R.id.tvBestDay);
        tvWorstDay = findViewById(R.id.tvWorstDay);
        chartCalories = findViewById(R.id.chartCalories);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Настраиваем график
        setupChart();
    }

    private void setupChart() {
        // Основные настройки графика
        chartCalories.getDescription().setEnabled(false);
        chartCalories.setTouchEnabled(true);
        chartCalories.setDragEnabled(true);
        chartCalories.setScaleEnabled(true);
        chartCalories.setPinchZoom(true);
        chartCalories.setDrawGridBackground(false);
        chartCalories.setBackgroundColor(Color.WHITE);

        // Настройка оси X
        XAxis xAxis = chartCalories.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.DKGRAY);

        // Настройка левой оси Y
        YAxis leftAxis = chartCalories.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#EEEEEE"));
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.DKGRAY);
        leftAxis.setAxisMinimum(0f);

        // Скрываем правую ось Y
        YAxis rightAxis = chartCalories.getAxisRight();
        rightAxis.setEnabled(false);

        // Легенда
        chartCalories.getLegend().setEnabled(false);
    }

    private void showRealWeeklyStats() {
        // Получаем все дни
        List<DayData> allDays = FoodDataManager.getInstance(getApplicationContext()).getAllDays();

        // Получаем данные за последние 7 дней
        List<DayData> lastWeekDays = getLastWeekDays(allDays);

        showWeekRange();
        setupChartData(lastWeekDays);
        calculateAndShowStats(lastWeekDays);
    }

    private List<DayData> getLastWeekDays(List<DayData> allDays) {
        List<DayData> lastWeek = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Создаем дни для всей недели (даже если данных нет)
        String[] dayNames = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date date = calendar.getTime();
            String dateKey = DateUtils.getDateKey(date);

            // Ищем данные для этого дня
            DayData foundDay = null;
            for (DayData day : allDays) {
                if (day.getDateKey().equals(dateKey)) {
                    foundDay = day;
                    break;
                }
            }

            if (foundDay == null) {
                // Создаем пустой день если данных нет
                foundDay = new DayData(date);
            }
            lastWeek.add(foundDay);
        }

        return lastWeek;
    }

    private void showWeekRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        Date startDate = calendar.getTime();

        Date endDate = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String range = sdf.format(startDate) + " - " + sdf.format(endDate);
        tvWeekRange.setText("Неделя: " + range);
    }

    private void setupChartData(List<DayData> weekDays) {
        if (weekDays.isEmpty()) return;

        List<Entry> entries = new ArrayList<>();
        List<String> dayLabels = new ArrayList<>();

        SimpleDateFormat dayFormat = new SimpleDateFormat("E", new Locale("ru"));

        for (int i = 0; i < weekDays.size(); i++) {
            DayData day = weekDays.get(i);
            entries.add(new Entry(i, day.getTotalCalories()));

            // Сокращенные названия дней недели
            String dayName = dayFormat.format(day.getDate());
            dayLabels.add(dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 2));
        }

        // Создаем набор данных для графика
        LineDataSet dataSet = new LineDataSet(entries, "Калории");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setCircleColor(Color.parseColor("#4CAF50"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setDrawValues(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Заполнение под линией
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#E8F5E8"));
        dataSet.setFillAlpha(100);

        LineData lineData = new LineData(dataSet);
        chartCalories.setData(lineData);

        // Устанавливаем подписи для оси X
        XAxis xAxis = chartCalories.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayLabels));

        // Обновляем график
        chartCalories.invalidate();
    }

    private void calculateAndShowStats(List<DayData> weekDays) {
        if (weekDays.isEmpty()) {
            showEmptyStats();
            return;
        }

        int totalCalories = 0;
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFat = 0;

        for (DayData day : weekDays) {
            totalCalories += day.getTotalCalories();
            totalProtein += day.getTotalProtein();
            totalCarbs += day.getTotalCarbs();
            totalFat += day.getTotalFat();
        }

        int daysCount = weekDays.size();
        int avgCalories = totalCalories / daysCount;
        int avgProtein = totalProtein / daysCount;
        int avgCarbs = totalCarbs / daysCount;
        int avgFat = totalFat / daysCount;

        DayData bestDay = getBestDay(weekDays);
        DayData worstDay = getWorstDay(weekDays);

        updateStatisticsUI(avgCalories, totalCalories, avgProtein, avgCarbs, avgFat, bestDay, worstDay);
    }

    private DayData getBestDay(List<DayData> days) {
        if (days.isEmpty()) return null;
        DayData best = days.get(0);
        for (DayData day : days) {
            if (day.getTotalCalories() < best.getTotalCalories()) {
                best = day;
            }
        }
        return best;
    }

    private DayData getWorstDay(List<DayData> days) {
        if (days.isEmpty()) return null;
        DayData worst = days.get(0);
        for (DayData day : days) {
            if (day.getTotalCalories() > worst.getTotalCalories()) {
                worst = day;
            }
        }
        return worst;
    }

    private void showEmptyStats() {
        tvAverageCalories.setText("Среднее: нет данных");
        tvTotalCalories.setText("Всего: 0 ккал");
        tvAverageProtein.setText("Белки: 0г/день");
        tvAverageCarbs.setText("Углеводы: 0г/день");
        tvAverageFat.setText("Жиры: 0г/день");
        tvBestDay.setText("Лучший день: нет данных");
        tvWorstDay.setText("Худший день: нет данных");

        // Очищаем график если нет данных
        chartCalories.clear();
        chartCalories.invalidate();
    }

    private void updateStatisticsUI(int avgCalories, int totalCalories, int avgProtein,
                                    int avgCarbs, int avgFat, DayData bestDay, DayData worstDay) {

        tvAverageCalories.setText("Среднее: " + avgCalories + " ккал/день");
        tvTotalCalories.setText("Всего: " + totalCalories + " ккал");
        tvAverageProtein.setText(avgProtein + "г/день");
        tvAverageCarbs.setText(avgCarbs + "г/день");
        tvAverageFat.setText(avgFat + "г/день");

        if (bestDay != null && worstDay != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("ru"));
            String bestDayName = sdf.format(bestDay.getDate());
            String worstDayName = sdf.format(worstDay.getDate());

            tvBestDay.setText("Лучший день: " + bestDayName + " (" + bestDay.getTotalCalories() + " ккал)");
            tvWorstDay.setText("Худший день: " + worstDayName + " (" + worstDay.getTotalCalories() + " ккал)");
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}