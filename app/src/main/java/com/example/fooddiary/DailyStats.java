package com.example.fooddiary;

import java.util.Date;

public class DailyStats {
    private Date date;
    private int totalCalories;
    private int totalProtein;
    private int totalCarbs;
    private int totalFat;
    private int foodItemsCount;

    public DailyStats(Date date, int totalCalories, int totalProtein, int totalCarbs, int totalFat, int foodItemsCount) {
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.foodItemsCount = foodItemsCount;
    }

    public Date getDate() { return date; }
    public int getTotalCalories() { return totalCalories; }
    public int getTotalProtein() { return totalProtein; }
    public int getTotalCarbs() { return totalCarbs; }
    public int getTotalFat() { return totalFat; }
    public int getFoodItemsCount() { return foodItemsCount; }

    public void setDate(Date date) { this.date = date; }
    public void setTotalCalories(int totalCalories) { this.totalCalories = totalCalories; }
    public void setTotalProtein(int totalProtein) { this.totalProtein = totalProtein; }
    public void setTotalCarbs(int totalCarbs) { this.totalCarbs = totalCarbs; }
    public void setTotalFat(int totalFat) { this.totalFat = totalFat; }
    public void setFoodItemsCount(int foodItemsCount) { this.foodItemsCount = foodItemsCount; }
}