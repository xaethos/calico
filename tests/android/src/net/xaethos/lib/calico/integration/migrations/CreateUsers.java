package net.xaethos.lib.calico.integration.migrations;

import android.database.sqlite.SQLiteDatabase;
import net.xaethos.lib.calico.content.ProviderMigration;

public class CreateUsers extends ProviderMigration {

    @Override
    public boolean onUpgrade(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY, name TEXT);");
        return true;
    }
}
