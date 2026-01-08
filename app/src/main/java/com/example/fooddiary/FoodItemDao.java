package com.example.fooddiary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FoodItemDao {

    @Insert
    long insert(FoodItemEntity foodItem);

    @Delete
    void delete(FoodItemEntity foodItem);

    @Query("DELETE FROM food_items WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM food_items ORDER BY date DESC")
    List<FoodItemEntity> getAll();

    @Query("SELECT * FROM food_items WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<FoodItemEntity> getByDateRange(long startDate, long endDate);

    @Query("SELECT * FROM food_items WHERE date = :date")
    List<FoodItemEntity> getByDate(long date);

    @Query("SELECT DISTINCT date FROM food_items ORDER BY date DESC")
    List<Long> getAllDates();
}