package com.example.fooddiary;

import android.content.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FoodDataManager {
    private static FoodDataManager instance;
    private Map<String, DayData> daysMap;
    private FoodRepository repository;

    private FoodDataManager(Context context) {
        daysMap = new LinkedHashMap<>();
        repository = new FoodRepository(context);
        loadAllData();
    }

    public static FoodDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new FoodDataManager(context);
        }
        return instance;
    }

    private void loadAllData() {
        repository.getAllFoodItems(new FoodRepository.RepositoryCallback<List<FoodItem>>() {
            @Override
            public void onSuccess(List<FoodItem> allItems) {
                daysMap.clear();
                for (FoodItem item : allItems) {
                    String dateKey = DateUtils.getDateKey(item.getDate());
                    DayData dayData = daysMap.get(dateKey);
                    if (dayData == null) {
                        dayData = new DayData(item.getDate());
                        daysMap.put(dateKey, dayData);
                    }
                    dayData.addFoodItem(item);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void addFoodItem(FoodItem item) {
        repository.insertFoodItem(item, new FoodRepository.RepositoryCallback<Long>() {
            @Override
            public void onSuccess(Long id) {
                item.setId(id.intValue());

                String dateKey = DateUtils.getDateKey(item.getDate());
                DayData dayData = daysMap.get(dateKey);
                if (dayData == null) {
                    dayData = new DayData(item.getDate());
                    daysMap.put(dateKey, dayData);
                }
                dayData.addFoodItem(item);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void removeFoodItem(FoodItem item) {
        repository.deleteFoodItem(item, new FoodRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                String dateKey = DateUtils.getDateKey(item.getDate());
                DayData dayData = daysMap.get(dateKey);
                if (dayData != null) {
                    dayData.removeFoodItem(item);

                    if (dayData.getFoodItemsCount() == 0) {
                        daysMap.remove(dateKey);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> allItems = new ArrayList<>();
        for (DayData dayData : daysMap.values()) {
            allItems.addAll(dayData.getFoodItems());
        }
        return allItems;
    }

    public List<DayData> getAllDays() {
        return new ArrayList<>(daysMap.values());
    }

    public DayData getTodayData() {
        String todayKey = DateUtils.getDateKey(DateUtils.getToday());
        DayData todayData = daysMap.get(todayKey);

        if (todayData == null) {
            todayData = new DayData(DateUtils.getToday());
            daysMap.put(todayKey, todayData);
        }

        return todayData;
    }

    public DayData getDayData(Date date) {
        String dateKey = DateUtils.getDateKey(date);
        return daysMap.get(dateKey);
    }

    public List<FoodItem> getFoodItemsForDay(Date date) {
        DayData dayData = getDayData(date);
        if (dayData != null) {
            return dayData.getFoodItems();
        }
        return new ArrayList<>();
    }

    public void clearAllData() {
        daysMap.clear();
    }
    public void reloadData() {
        loadAllData();
    }
}