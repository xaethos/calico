package com.example.fixtures;

import android.database.sqlite.SQLiteDatabase;
import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.content.ActiveMigration;
import net.xaethos.lib.activeprovider.content.ActiveProvider;

@ProviderInfo(
        databaseName = "test",
        models = { Data.class },
        migrations = {
                DataProvider.Migration1.class,
                DataProvider.Migration2.class,
                DataProvider.Migration3.class
        }
)
public class DataProvider extends ActiveProvider {

    public static class Migration1 extends ActiveMigration {
        @Override public boolean onUpgrade(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE data (" +
                    "_id INTEGER PRIMARY KEY," +
                    "string TEXT," +
                    "bool INTEGER," +
                    "byte INTEGER," +
                    "short INTEGER," +
                    "int INTEGER," +
                    "long INTEGER," +
                    "float REAL," +
                    "double REAL," +
                    "data BLOB," +
                    "timestamp INTEGER," +
                    "created_at INTEGER," +
                    "updated_at INTEGER);");
            return true;
        }
    }

    public static class Migration2 extends ActiveMigration {
        @Override public boolean onUpgrade(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE temp (_id INTEGER PRIMARY KEY);");
            return true;
        }
    }

    public static class Migration3 extends ActiveMigration {
        @Override public boolean onUpgrade(SQLiteDatabase db) {
            db.execSQL("DELETE FROM temp; DROP TABLE temp;");
            return true;
        }
    }

}
