package com.foxconn.bandon.messages.model;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MemoDao {

    @Query("SELECT * FROM Memos")
    List<Memo> getMemos();

    @Query("SELECT * FROM Memos WHERE id = :id")
    Memo getMemoById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMemo(Memo memo);

    @Query("DELETE FROM Memos WHERE id = :id")
    void deleteMemoById(String id);

    @Update
    int updateMemo(Memo memo);

    @Query("DELETE FROM Memos")
    void deleteAllMemos();

}
