package com.example.fooddiary;

import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodRepository {
    private FoodItemDao foodItemDao;
    private ExecutorService executor;

    public FoodRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        foodItemDao = database.foodItemDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertFoodItem(FoodItem foodItem, RepositoryCallback<Long> callback) {
        executor.execute(() -> {
            try {
                FoodItemEntity entity = new FoodItemEntity(foodItem);
                long id = foodItemDao.insert(entity);
                System.out.println("✅ Продукт сохранен в базу. ID: " + id);

                foodItem.setId((int) id);

                callback.onSuccess(id);
            } catch (Exception e) {
                System.out.println("❌ Ошибка сохранения: " + e.getMessage());
                callback.onError(e);
            }
        });
    }

    public void deleteFoodItem(FoodItem foodItem, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            try {
                FoodItemEntity entity = new FoodItemEntity(foodItem);
                entity.id = foodItem.getId();
                foodItemDao.delete(entity);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getAllFoodItems(RepositoryCallback<List<FoodItem>> callback) {
        executor.execute(() -> {
            try {
                List<FoodItemEntity> entities = foodItemDao.getAll();
                List<FoodItem> foodItems = new ArrayList<>();
                for (FoodItemEntity entity : entities) {
                    foodItems.add(new FoodItem(entity));
                }
                callback.onSuccess(foodItems);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    public void getFoodItemsByDate(Date date, RepositoryCallback<List<FoodItem>> callback) {
        executor.execute(() -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long startDate = calendar.getTimeInMillis();

                calendar.add(Calendar.DAY_OF_YEAR, 1);
                long endDate = calendar.getTimeInMillis();

                List<FoodItemEntity> entities = foodItemDao.getByDateRange(startDate, endDate);
                List<FoodItem> foodItems = new ArrayList<>();
                for (FoodItemEntity entity : entities) {
                    foodItems.add(new FoodItem(entity));
                }
                callback.onSuccess(foodItems);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getAllDates(RepositoryCallback<List<Date>> callback) {
        executor.execute(() -> {
            try {
                List<Long> dateTimestamps = foodItemDao.getAllDates();
                List<Date> dates = new ArrayList<>();
                for (Long timestamp : dateTimestamps) {
                    dates.add(new Date(timestamp));
                }
                callback.onSuccess(dates);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}