package com.foxconn.bandon.base;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.foxconn.bandon.gtm.model.ExceptionMessage;
import com.foxconn.bandon.gtm.model.FoodMessage;
import com.foxconn.bandon.gtm.model.GTMDao;
import com.foxconn.bandon.gtm.model.WeatherMessage;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoDao;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockDao;
import com.foxconn.bandon.setting.user.model.UserDao;
import com.foxconn.bandon.setting.user.model.UserInfo;

@Database(entities = {ClockBean.class, Memo.class, FoodMessage.class, WeatherMessage.class, ExceptionMessage.class, UserInfo.class},
        version = 1, exportSchema = false)
public abstract class BandonDataBase extends RoomDatabase {
    private static BandonDataBase INSTANCE;

    public abstract ClockDao clockDao();

    public abstract MemoDao memoDao();

    public abstract GTMDao gtmDao();

    public abstract UserDao getUserDao();

    private static final Object sLock = new Object();

    public static BandonDataBase getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (sLock) {
                if (null == INSTANCE) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BandonDataBase.class, "Bandon.db")
                            //.addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE WeatherMessage " +
                    "(id TEXT NOT NULL," +
                    "level INTEGER NOT NULL DEFAULT 0," +
                    "device_id TEXT, content TEXT, " +
                    "type INTEGER NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE ExceptionMessage " +
                    "(id TEXT NOT NULL," +
                    "level INTEGER NOT NULL DEFAULT 0," +
                    "device_id TEXT, content TEXT, " +
                    "type INTEGER NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE FoodMessage(tid INTEGER NOT NULL," +
                    "id TEXT NOT NULL," +
                    "level INTEGER NOT NULL DEFAULT 2," +
                    "device_id TEXT, content TEXT, " +
                    "type INTEGER NOT NULL DEFAULT 1," +
                    "position INTEGER NOT NULL DEFAULT 0," +
                    "food_name TEXT," +
                    "expiration_date TEXT,PRIMARY KEY(id))");
        }
    };
}
