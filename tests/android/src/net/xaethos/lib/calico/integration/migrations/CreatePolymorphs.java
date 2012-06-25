package net.xaethos.lib.calico.integration.migrations;

import android.database.sqlite.SQLiteDatabase;
import net.xaethos.lib.calico.content.ProviderMigration;

public class CreatePolymorphs extends ProviderMigration {
    @Override
    public boolean onUpgrade(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE polymorphs(_id INTEGER PRIMARY KEY, value);");
        return true;
    }
}
