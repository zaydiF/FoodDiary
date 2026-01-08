package com.example.fooddiary;

public class UserProfile {
    private String name;
    private int age;
    private int height;
    private double weight;
    private String goal; // "lose", "maintain", "gain"
    private int activityLevel; // 1-5
    private int dailyCalorieGoal;

    public UserProfile() {}

    public UserProfile(String name, int age, int height, double weight, String goal, int activityLevel) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.goal = goal;
        this.activityLevel = activityLevel;
        this.dailyCalorieGoal = calculateCalorieGoal();
    }

    private int calculateCalorieGoal() {
        // Формула Миффлина-Сан Жеора
        double bmr;
        if (age < 18) {
            // Упрощенная формула для подростков
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            // Стандартная формула
            bmr = 10 * weight + 6.25 * height - 5 * age;
            bmr += goal.equals("lose") ? -161 : 5; // -161 для женщин, +5 для мужчин (упрощенно)
        }

        // Умножаем на коэффициент активности
        double activityMultiplier = getActivityMultiplier();
        double maintenanceCalories = bmr * activityMultiplier;

        // Корректируем по цели
        switch (goal) {
            case "lose":
                return (int) (maintenanceCalories - 500); // Дефицит 500 ккал
            case "gain":
                return (int) (maintenanceCalories + 500); // Профицит 500 ккал
            default: // maintain
                return (int) maintenanceCalories;
        }
    }

    private double getActivityMultiplier() {
        switch (activityLevel) {
            case 1: return 1.2;   // Сидячий образ жизни
            case 2: return 1.375; // Легкая активность (1-3 раза в неделю)
            case 3: return 1.55;  // Средняя активность (3-5 раз в неделю)
            case 4: return 1.725; // Высокая активность (6-7 раз в неделю)
            case 5: return 1.9;   // Очень высокая активность
            default: return 1.2;
        }
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; this.dailyCalorieGoal = calculateCalorieGoal(); }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; this.dailyCalorieGoal = calculateCalorieGoal(); }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; this.dailyCalorieGoal = calculateCalorieGoal(); }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; this.dailyCalorieGoal = calculateCalorieGoal(); }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; this.dailyCalorieGoal = calculateCalorieGoal(); }

    public int getActivityLevel() { return activityLevel; }
    public void setActivityLevel(int activityLevel) { this.activityLevel = activityLevel; this.dailyCalorieGoal = calculateCalorieGoal(); }

    public int getDailyCalorieGoal() { return dailyCalorieGoal; }

    public String getGoalDisplayName() {
        switch (goal) {
            case "lose": return "Похудение";
            case "maintain": return "Поддержание веса";
            case "gain": return "Набор массы";
            default: return "Не указано";
        }
    }

    public String getActivityDisplayName() {
        switch (activityLevel) {
            case 1: return "Сидячий образ жизни";
            case 2: return "Легкая активность";
            case 3: return "Средняя активность";
            case 4: return "Высокая активность";
            case 5: return "Очень высокая активность";
            default: return "Не указано";
        }
    }
}