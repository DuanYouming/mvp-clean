package com.foxconn.bandon.setting.clock.model;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.foxconn.bandon.setting.clock.model.ClockBean;

import java.util.List;

@Dao
public interface ClockDao {

    @Query("SELECT * FROM Clocks")
    List<ClockBean> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addClock(ClockBean clock);

    @Query("DELETE FROM Clocks WHERE id = :id")
    void deleteClock(String id);

    @Update
    int update(ClockBean clock);

    @Query("SELECT * FROM Clocks WHERE id = :id")
    ClockBean getClock(String id);

}
