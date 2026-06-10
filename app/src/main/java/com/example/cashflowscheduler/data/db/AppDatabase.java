package com.example.cashflowscheduler.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cashflowscheduler.data.model.Expense;
import com.example.cashflowscheduler.data.model.Income;
import com.example.cashflowscheduler.data.model.User;

@Database(entities = {User.class, Income.class, Expense.class},
        version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao    userDao();
    public abstract FinanceDao financeDao();

    private static volatile AppDatabase INSTANCE;

    // version 1 → 2: expenses 테이블에 memo 컬럼 추가
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE expenses ADD COLUMN memo TEXT NOT NULL DEFAULT ''");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "cash_flow_db")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}