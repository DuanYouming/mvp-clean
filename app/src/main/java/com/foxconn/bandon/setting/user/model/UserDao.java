package com.foxconn.bandon.setting.user.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<UserInfo> getUsers();

    @Query("SELECT *  FROM user where name = :name")
    UserInfo getUser(String name);

    @Insert
    void insertUser(UserInfo user);

    @Update
    void updateUser(UserInfo user);

    @Query("Delete FROM user WHERE name = :name")
    void deleteUser(String name);

}
