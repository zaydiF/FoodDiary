package com.example.fooddiary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "food_items")
public class FoodItemEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int weight;
    public int calories;
    public int protein;
    public int carbs;
    public int fat;
    public String imagePath;
    public long date;

    public FoodItemEntity() {}

    public FoodItemEntity(FoodItem foodItem) {
        this.name = foodItem.getName();
        this.weight = foodItem.getWeight();
        this.calories = foodItem.getCalories();
        this.protein = foodItem.getProtein();
        this.carbs = foodItem.getCarbs();
        this.fat = foodItem.getFat();
        this.imagePath = foodItem.getImagePath();
        this.date = foodItem.getDate().getTime();
    }

    public FoodItem toFoodItem() {
        return new FoodItem(name, weight, calories, protein, carbs, fat, imagePath, new Date(date));
    }
}