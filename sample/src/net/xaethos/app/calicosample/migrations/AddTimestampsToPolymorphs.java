package net.xaethos.app.calicosample.migrations;

import android.database.sqlite.SQLiteDatabase;
import net.xaethos.lib.calico.content.ProviderMigration;

public class AddTimestampsToPolymorphs extends ProviderMigration {
    @Override
    public boolean onUpgrade(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE polymorphs ADD COLUMN _created_at INTEGER;");
        db.execSQL("ALTER TABLE polymorphs ADD COLUMN _updated_at INTEGER;");
        return true;
    }
}
