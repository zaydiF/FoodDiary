package com.example.fooddiary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DayData {
    private Date date;
    private String dateKey;
    private List<FoodItem> foodItems;
    private int totalCalories;
    private int totalProtein;
    private int totalCarbs;
    private int totalFat;

    public DayData(Date date) {
        this.date = DateUtils.getDateWithoutTime(date);
        this.dateKey = DateUtils.getDateKey(this.date);
        this.foodItems = new ArrayList<>();
        this.totalCalories = 0;
        this.totalProtein = 0;
        this.totalCarbs = 0;
        this.totalFat = 0;
    }

    public void addFoodItem(FoodItem item) {
        foodItems.add(item);
        recalculateTotals();
    }

    public void removeFoodItem(int position) {
        if (position >= 0 && position < foodItems.size()) {
            foodItems.remove(position);
            recalculateTotals();
        }
    }

    public void removeFoodItem(FoodItem item) {
        foodItems.remove(item);
        recalculateTotals();
    }

    private void recalculateTotals() {
        totalCalories = 0;
        totalProtein = 0;
        totalCarbs = 0;
        totalFat = 0;

        for (FoodItem item : foodItems) {
            totalCalories += item.getCalories();
            totalProtein += item.getProtein();
            totalCarbs += item.getCarbs();
            totalFat += item.getFat();
        }
    }

    // Геттеры
    public Date getDate() { return date; }
    public String getDateKey() { return dateKey; }
    public List<FoodItem> getFoodItems() { return foodItems; }
    public int getTotalCalories() { return totalCalories; }
    public int getTotalProtein() { return totalProtein; }
    public int getTotalCarbs() { return totalCarbs; }
    public int getTotalFat() { return totalFat; }
    public int getFoodItemsCount() { return foodItems.size(); }

    public String getDisplayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        return sdf.format(date);
    }

    public String getDayName() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("ru"));
        return sdf.format(date);
    }


}