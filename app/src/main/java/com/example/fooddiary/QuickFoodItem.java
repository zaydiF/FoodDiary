package com.example.fooddiary;

import java.util.UUID;

public class QuickFoodItem {
    private String id;
    private String name;
    private int weight;
    private int calories;
    private int protein;
    private int carbs;
    private int fat;
    private String emoji;

    // Конструктор для новых продуктов
    public QuickFoodItem(String name, int weight, int calories, int protein, int carbs, int fat, String emoji) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.weight = weight;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.emoji = emoji;
    }

    // Конструктор для существующих продуктов (с ID)
    public QuickFoodItem(String id, String name, int weight, int calories, int protein, int carbs, int fat, String emoji) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.emoji = emoji;
    }

    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public int getWeight() { return weight; }
    public int getCalories() { return calories; }
    public int getProtein() { return protein; }
    public int getCarbs() { return carbs; }
    public int getFat() { return fat; }
    public String getEmoji() { return emoji; }
}