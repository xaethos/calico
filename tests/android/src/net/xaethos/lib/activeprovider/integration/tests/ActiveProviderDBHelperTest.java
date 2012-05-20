package net.xaethos.lib.activeprovider.integration.tests;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.mock.MockContext;
import junit.framework.TestCase;
import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.content.ActiveMigration;
import net.xaethos.lib.activeprovider.content.ActiveProvider;

import java.util.Arrays;
import java.util.List;

import static net.xaethos.lib.activeprovider.integration.tests.Assert.assertHasItems;

public class ActiveProviderDBHelperTest extends TestCase {

    @ProviderInfo(
            databaseName = "migration_test.db",
            models = {},
            migrations = {
                    TestProvider.Migration1.class,
                    TestProvider.Migration2.class
            }
    )
    public static class TestProvider extends ActiveProvider {
        public static class Migration1 extends ActiveMigration {
            public static boolean wasRun = false;
            @Override public boolean onUpgrade(SQLiteDatabase db) {
                wasRun = true;
                return true;
            }
        }
        public static class Migration2 extends ActiveMigration {
            public static boolean wasRun = false;
            @Override public boolean onUpgrade(SQLiteDatabase db) {
                wasRun = true;
                return true;
            }
        }
    }

    ActiveProvider.DBHelper helper;
    SQLiteDatabase db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = new ActiveProvider.DBHelper(new MockContext(), TestProvider.class.getAnnotation(ProviderInfo.class));
        db = SQLiteDatabase.create(null);
    }

    ////////// Tests //////////

    public void test_onOpen_queriesTableNames() {
        db.execSQL("CREATE TABLE foo (bar);");

        assertNull(helper.getTableNames());
        helper.onOpen(db);

        String[] tableNames = helper.getTableNames();
        assertEquals(2, tableNames.length);
        assertHasItems(tableNames, "foo", "android_metadata");
    }

    public void test_onUpgrade_runsMissingMigrations() {
        ActiveProvider.DBHelper.createMigrationsTable(db);

        TestProvider.Migration1.wasRun = false;
        TestProvider.Migration2.wasRun = false;
        helper.onUpgrade(db, db.getVersion(), helper.getProviderVersion());
        assertTrue(TestProvider.Migration1.wasRun);
        assertTrue(TestProvider.Migration2.wasRun);
    }

    public void test_onCreate_createsMigrationsTable() {
        assertFalse(Arrays.asList(ActiveProvider.DBHelper.queryTableNames(db))
                .contains(ActiveProvider.MIGRATIONS_TABLE));
        helper.onCreate(db);
        assertTrue(Arrays.asList(ActiveProvider.DBHelper.queryTableNames(db))
                .contains(ActiveProvider.MIGRATIONS_TABLE));
    }

    public void test_queryTableNames() {
        db.execSQL("CREATE TABLE foo (bar);");

        String[] tableNames = ActiveProvider.DBHelper.queryTableNames(db);
        assertEquals(2, tableNames.length);
        assertHasItems(tableNames, "foo", "android_metadata");
    }

    public void test_getMissingMigrations() {
        ActiveProvider.DBHelper.createMigrationsTable(db);
        ContentValues values = new ContentValues(1); values.put("name", "Migration2");
        db.insert(ActiveProvider.MIGRATIONS_TABLE, null, values);

        List<ActiveMigration> migrations = helper.getMissingMigrations(db);
        assertEquals(1, migrations.size());
        assertTrue(migrations.get(0) instanceof TestProvider.Migration1);
    }

}
