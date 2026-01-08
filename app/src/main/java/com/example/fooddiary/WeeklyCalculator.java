package com.example.fooddiary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyCalculator {

    public static List<DailyStats> calculateWeeklyStats(List<FoodItem> allFoodItems, Date targetDate) {
        Map<String, DailyStats> dailyStatsMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        // Определяем начало и конец недели (понедельник - воскресенье)
        calendar.setTime(targetDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date weekStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEnd = calendar.getTime();

        // Группируем продукты по дням
        for (FoodItem item : allFoodItems) {
            Date itemDate = item.getDate();
            if (!itemDate.before(weekStart) && !itemDate.after(weekEnd)) {
                String dateKey = getDateKey(itemDate);

                DailyStats dayStats = dailyStatsMap.get(dateKey);
                if (dayStats == null) {
                    dayStats = new DailyStats(itemDate, 0, 0, 0, 0, 0);
                    dailyStatsMap.put(dateKey, dayStats);
                }

                // Суммируем показатели
                dayStats.setTotalCalories(dayStats.getTotalCalories() + item.getCalories());
                dayStats.setTotalProtein(dayStats.getTotalProtein() + item.getProtein());
                dayStats.setTotalCarbs(dayStats.getTotalCarbs() + item.getCarbs());
                dayStats.setTotalFat(dayStats.getTotalFat() + item.getFat());
                dayStats.setFoodItemsCount(dayStats.getFoodItemsCount() + 1);
            }
        }

        return new ArrayList<>(dailyStatsMap.values());
    }

    private static String getDateKey(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    }

    public static DailyStats getBestDay(List<DailyStats> weeklyStats) {
        if (weeklyStats.isEmpty()) return null;

        DailyStats bestDay = weeklyStats.get(0);
        for (DailyStats day : weeklyStats) {
            if (day.getTotalCalories() < bestDay.getTotalCalories()) {
                bestDay = day;
            }
        }
        return bestDay;
    }

    public static DailyStats getWorstDay(List<DailyStats> weeklyStats) {
        if (weeklyStats.isEmpty()) return null;

        DailyStats worstDay = weeklyStats.get(0);
        for (DailyStats day : weeklyStats) {
            if (day.getTotalCalories() > worstDay.getTotalCalories()) {
                worstDay = day;
            }
        }
        return worstDay;
    }
}