package com.example.fooddiary;

import java.util.Date;

public class FoodItem {
    private int id;
    private String name;
    private int weight;
    private int calories;
    private int protein;
    private int carbs;
    private int fat;
    private String imagePath;
    private Date date;

    public FoodItem(String name, int weight, int calories, int protein, int carbs, int fat, Date date) {
        this.name = name;
        this.weight = weight;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.imagePath = null;
        this.date = date;
    }

    public FoodItem(String name, int weight, int calories, int protein, int carbs, int fat, String imagePath, Date date) {
        this.name = name;
        this.weight = weight;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.imagePath = imagePath;
        this.date = date;
    }

    public FoodItem(FoodItemEntity entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.weight = entity.weight;
        this.calories = entity.calories;
        this.protein = entity.protein;
        this.carbs = entity.carbs;
        this.fat = entity.fat;
        this.imagePath = entity.imagePath;
        this.date = new Date(entity.date);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getProtein() { return protein; }
    public void setProtein(int protein) { this.protein = protein; }

    public int getCarbs() { return carbs; }
    public void setCarbs(int carbs) { this.carbs = carbs; }

    public int getFat() { return fat; }
    public void setFat(int fat) { this.fat = fat; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}