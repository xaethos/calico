package net.xaethos.lib.activeprovider.integration.migrations;

import android.database.sqlite.SQLiteDatabase;
import net.xaethos.lib.activeprovider.content.ActiveMigration;

public class CreatePolymorphs extends ActiveMigration {
    @Override
    public boolean onUpgrade(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE polymorphs(_id INTEGER PRIMARY KEY, value);");
        return true;
    }
}
