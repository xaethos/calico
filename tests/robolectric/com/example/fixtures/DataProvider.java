package com.example.fixtures;

import android.database.sqlite.SQLiteDatabase;
import net.xaethos.lib.calico.annotations.ProviderInfo;
import net.xaethos.lib.calico.content.ProviderMigration;
import net.xaethos.lib.calico.content.CalicoProvider;

@ProviderInfo(
        databaseName = "test",
        models = { Data.class },
        migrations = {
                DataProvider.Migration1.class,
                DataProvider.Migration2.class,
                DataProvider.Migration3.class
        }
)
public class DataProvider extends CalicoProvider {

    public static class Migration1 extends ProviderMigration {
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
                    "_created_at INTEGER," +
                    "_updated_at INTEGER);");
            return true;
        }
    }

    public static class Migration2 extends ProviderMigration {
        @Override public boolean onUpgrade(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE temp (_id INTEGER PRIMARY KEY);");
            return true;
        }
    }

    public static class Migration3 extends ProviderMigration {
        @Override public boolean onUpgrade(SQLiteDatabase db) {
            db.execSQL("DELETE FROM temp; DROP TABLE temp;");
            return true;
        }
    }

}
