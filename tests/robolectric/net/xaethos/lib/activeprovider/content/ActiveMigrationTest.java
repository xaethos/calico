package net.xaethos.lib.activeprovider.content;

import android.database.sqlite.SQLiteDatabase;
import com.example.fixtures.DataProvider;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class ActiveMigrationTest {

	/////////////// Constants ///////////////

	///// Database constants
//	private static final String SCHEMA = "PUBLIC";

//	private static final int TABLE_NAME = 3;
//	private static final int TABLE_TYPE = 4;

//	private static final int COLUMN_NAME = 4;

	/////////////// Fields ///////////////

	SQLiteDatabase db;
    ActiveProvider.DBHelper helper;

	/////////////// Set up ///////////////

	@Before public void setup() {
		db = SQLiteDatabase.openDatabase(null, null, 0);
        helper = new ActiveProvider.DBHelper(Robolectric.application,
                DataProvider.class.getAnnotation(ProviderInfo.class));
        helper.onCreate(db);
    }

	/////////////// Tests ///////////////

	@Test public void callsOnUpgrade() {
		class TestMigration extends ActiveMigration {
			public boolean onUpgradeCalled;
			@Override public boolean onUpgrade(SQLiteDatabase db) {
				onUpgradeCalled = true;
				return true;
			}
		}
		TestMigration migration = new TestMigration();
		migration.upgrade(db);

		assertThat(migration.onUpgradeCalled, is(true));
	}

	@Test(expected=NullPointerException.class)
	public void mustHaveADatabase() {
		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) { return true; }
		}.upgrade(null);
	}

	@Test(expected=MigrationException.class)
	public void balksAtMigrationFailure() {
		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) { return false; }
		}.upgrade(db);
	}

    @Test
    public void upgrade_marksMigrationAsApplied() {
        List<ActiveMigration> migrations = helper.getMissingMigrations(db);
        assertFalse(migrations.isEmpty());
        for (ActiveMigration migration : migrations) migration.upgrade(db);
        assertTrue(helper.getMissingMigrations(db).isEmpty());
    }

    @Test
    public void onUpgrade_happensInTransaction() {
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        new ActiveMigration() {
            @Override public boolean onUpgrade(SQLiteDatabase db) {
                verify(db, times(1)).beginTransaction();
                verify(db, never()).setTransactionSuccessful();
                verify(db, never()).endTransaction();
                return true;
            }
        }.upgrade(mockDB);

        verify(mockDB, times(1)).beginTransaction();
        verify(mockDB, times(1)).setTransactionSuccessful();
        verify(mockDB, times(1)).endTransaction();
    }

    @Test
    public void onUpgrade_rollsBackIfFalse() {
        SQLiteDatabase mockDB = mock(SQLiteDatabase.class);
        try {
        new ActiveMigration() {
            @Override public boolean onUpgrade(SQLiteDatabase db) {
                verify(db, times(1)).beginTransaction();
                verify(db, never()).setTransactionSuccessful();
                verify(db, never()).endTransaction();
                return false;
            }
        }.upgrade(mockDB);
        }
        catch (MigrationException e) {}

        verify(mockDB, times(1)).beginTransaction();
        verify(mockDB, never()).setTransactionSuccessful();
        verify(mockDB, times(1)).endTransaction();
    }

    /*
	@Test public void canCreateTable() throws Exception {
		createTestTable();
		ResultSet results = getTables("foo");
		assertThat(results.first(), is(true));
		assertThat(results.getString(TABLE_NAME), is("FOO"));
		assertThat(results.getString(TABLE_TYPE), is("TABLE"));
	}

	@Test public void canDropTable() throws Exception {
		createTestTable();
		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) {
				dropTable("foo");
				return true;
			}
		}.upgrade(db);

		ResultSet results = getTables("foo");
		assertThat(results.first(), is(false));
	}

	@Test public void canRenameTable() throws Exception {
		createTestTable();
		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) {
				renameTable("foo", "bar");
				return true;
			}
		}.upgrade(db);

		ResultSet results = getTables("%");
		assertThat(results.first(), is(true));
		assertThat(results.getString(TABLE_NAME), is("BAR"));
		assertThat(results.isLast(), is(true));
	}

	@Test public void canAddColumn() throws Exception {
		createTestTable();
		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) {
				addColumn("foo", Column.text("bar"));
				return true;
			}
		}.upgrade(db);

		ResultSet results = getColumns("foo");
		assertThat(results.absolute(2), is(true));
		assertThat(results.getString(COLUMN_NAME), is("BAR"));
		assertThat(results.isLast(), is(true));
	}

	@Test public void addColumnRetainsRows() {
		createTestTable("table", Column.text("name"));
		insertValues("table", "_id", "1", "name", "Alice");

		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) {
				addColumn("table", Column.text("surname"));
				return true;
			}
		}.upgrade(db);

		Cursor cursor = db.query(
				"table", new String[]{"_id", "name", "surname"}, null, null, null, null, null);
		assertThat(cursor.moveToFirst(), is(true));
		//assertThat(cursor.getLong(0), is(1L)); TODO figure out how to test id
		assertThat(cursor.getString(1), is("Alice"));
		assertThat(cursor.getString(2), is(nullValue()));
	}

	@Test public void canDropColumn() throws Exception {
		createTestTable("foo",
				Column.text("bar"),
				Column.text("cat"),
				Column.text("dog"));

		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) {
				dropColumn("foo", "bar", "dog");
				return true;
			}
		}.upgrade(db);

		ResultSet results = getColumns("foo");
		assertThat(results.first(), is(true));
		assertThat(results.getString(COLUMN_NAME), is("_ID"));
		assertThat(results.next(), is(true));
		assertThat(results.getString(COLUMN_NAME), is("CAT"));
		assertThat(results.isLast(), is(true));
	}
	*/

	/* http://api.rubyonrails.org/classes/ActiveRecord/Migration.html
TODO rename_column(table_name, column_name, new_column_name):
 Renames a column but keeps the type and content.
TODO change_column(table_name, column_name, type, options):
 Changes the column to a different type using the same parameters as add_column.
TODO remove_column(table_name, column_name):
 Removes the column named column_name from the table called table_name.
	 */

	///// Helper methods

    /*
	private void createTestTable() {
		createTestTable("foo");
	}

	private void createTestTable(final String tableName, final Column...columns) {
		new ActiveMigration() {
			@Override public boolean onUpgrade(SQLiteDatabase db) {
				createTable(tableName, columns);
				return true;
			}
		}.upgrade(db);
	}

	private long insertValues(String tableName, String...keysAndValues) {
		ContentValues values = new ContentValues();
		for (int i=0; i<keysAndValues.length; i+=2) {
			values.put(keysAndValues[i], keysAndValues[i+1]);
		}
		return db.insert(tableName, null, values);
	}

	private DatabaseMetaData getDatabaseMetaData() throws SQLException {
		return shadowOf(db).getConnection().getMetaData();
	}

	private ResultSet getTables(String tableNamePattern) throws SQLException {
		return getDatabaseMetaData().getTables(
				null, SCHEMA, tableNamePattern.toUpperCase(), null);
	}

	private ResultSet getColumns(String tableNamePattern) throws SQLException {
		return getDatabaseMetaData().getColumns(
				null, SCHEMA, tableNamePattern.toUpperCase(), "%");
	}
	*/

}
