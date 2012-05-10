package net.xaethos.lib.activeprovider.integration.migrations;

import android.database.sqlite.SQLiteDatabase;
import net.xaethos.lib.activeprovider.content.ActiveMigration;

public class CreateUsers extends ActiveMigration {

    @Override
    public boolean onUpgrade(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY, name TEXT);");
        return true;
    }
}
