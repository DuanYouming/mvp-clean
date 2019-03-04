package com.foxconn.bandon.gtm.model;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.List;

@Dao
public interface GTMDao {

    //food message
    @Query("SELECT * FROM FoodMessage")
    List<FoodMessage> getFoodMessages();

    @Insert
    void insertFoodMessage(FoodMessage foodMessage);

    @Update
    void updateFoodMessage(FoodMessage message);

    @Query("Delete FROM FoodMessage WHERE id = :id")
    void deleteFoodMessage(String id);

    @Query("SELECT * FROM FoodMessage WHERE tid = :tid")
    FoodMessage getFoodMessage(int tid);

    //exception message
    @Query("SELECT * FROM ExceptionMessage")
    List<ExceptionMessage> getExceptionMessages();

    @Insert
    void insertExceptionMessage(ExceptionMessage message);

    @Update
    void updateExceptionMessage(ExceptionMessage message);

    @Query("Delete FROM ExceptionMessage WHERE id = :id")
    void deleteExceptionMessage(String id);

    //weather message
    @Query("SELECT * FROM WeatherMessage")
    List<WeatherMessage> getWeatherMessages();

    @Insert
    void insertWeatherMessage(WeatherMessage message);

    @Update
    void updateWeatherMessage(WeatherMessage message);

    @Query("Delete FROM WeatherMessage WHERE id = :id")
    void deleteWeatherMessage(String id);

}
