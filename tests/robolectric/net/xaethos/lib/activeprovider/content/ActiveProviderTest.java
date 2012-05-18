package net.xaethos.lib.activeprovider.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.fixtures.Data;
import com.example.fixtures.DataProvider;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContentResolver;
import com.xtremelabs.robolectric.shadows.ShadowSQLiteCursor;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class ActiveProviderTest {

	DataProvider provider;

	ContentResolver resolver;
	ShadowContentResolver resolverShadow;

    ModelInfo dataInfo;
    Uri dirUri;
    Uri itemUri;

    /////////////// Set up ///////////////

	@Before public void instantiateProvider() {
		provider = new DataProvider();
        provider.onCreate();
	}

    @Before public void getModelInfo() {
        dataInfo = Data.class.getAnnotation(ModelInfo.class);

        String authority = dataInfo.authority();
        String tableName = dataInfo.tableName();

        dirUri = Uri.parse("content://" + authority + "/" + tableName);
        itemUri = Uri.parse("content://" + authority + "/" + tableName + "/1");
    }

	@Before public void getResolver() {
		resolver = Robolectric.application.getContentResolver();
		resolverShadow = shadowOf(resolver);
	}

	/////////////// Tests ///////////////

	@Test public void querySetsNotificationUri() {
		SQLiteCursor cursor =
				(SQLiteCursor) provider.query(dirUri, null, null, null, null);
		ShadowSQLiteCursor shadow = shadowOf(cursor);
		assertThat(shadow.getNotificationUri_Compatibility(), is(dirUri));
	}

	@Test public void insertNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");

		resolverShadow.getNotifiedUris().clear();
		provider.insert(dirUri, values);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();
		assertThat(notifiedUris.size(), is(1));
		assertThat(notifiedUris.get(0).uri, is(dirUri));
	}

	@Test public void updateNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");
		Uri uri = provider.insert(dirUri, values);

		resolverShadow.getNotifiedUris().clear();
		values.put(Data.STRING, "Goodbye");
		provider.update(uri, values, null, null);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();

        assertThat(notifiedUris.size(), is(1));
        assertThat(notifiedUris.get(0).uri, is(uri));
    }

	@Test public void deleteNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");
		Uri uri = provider.insert(dirUri, values);

		resolverShadow.getNotifiedUris().clear();
		provider.delete(uri, null, null);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();

        assertThat(notifiedUris.size(), is(1));
        assertThat(notifiedUris.get(0).uri, is(uri));
    }

	/////////////// Helpers ///////////////

    @RunWith(RobolectricTestRunner.class)
    public static class DBHelper {

        Context context;
        ProviderInfo info;
        ActiveProvider.DBHelper helper;

        @Before public void setup() {
            context = Robolectric.application;
            info = DataProvider.class.getAnnotation(ProviderInfo.class);
            helper = new ActiveProvider.DBHelper(context, info);
        }

        @Test
        public void onOpen_queriesTableNames() {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(null, null, 0);
            db.execSQL("CREATE TABLE foo (bar);");

            assertNull(helper.getTableNames());
            helper.onOpen(db);

            String[] tableNames = helper.getTableNames();
            assertThat(tableNames.length, is(1));
            assertThat(tableNames[0], is("foo"));
        }

        @Test
        public void onUpgrade_runsMissingMigrations() {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(null, null, 0);
            ActiveProvider.DBHelper.createMigrationsTable(db);

            DataProvider.Migration1 m1 = mock(DataProvider.Migration1.class);
            DataProvider.Migration2 m2 = mock(DataProvider.Migration2.class);

            ActiveProvider.DBHelper helperSpy = spy(helper);
            when(helperSpy.getMissingMigrations(db)).thenReturn(Arrays.asList(m1, m2));

            helperSpy.onUpgrade(db, db.getVersion(), helper.getProviderVersion());
            verify(m1).upgrade(db);
            verify(m2).upgrade(db);
        }

        @Test public void onCreate_createsMigrationsTable() {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(null, null, 0);
            assertThat(Arrays.asList(ActiveProvider.DBHelper.queryTableNames(db)),
                    not(hasItem(ActiveProvider.MIGRATIONS_TABLE)));
            helper.onCreate(db);
            assertThat(Arrays.asList(ActiveProvider.DBHelper.queryTableNames(db)),
                    hasItem(ActiveProvider.MIGRATIONS_TABLE));
        }

        @Test public void queryTableNames() {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(null, null, 0);
            db.execSQL("CREATE TABLE foo (bar);");

            String[] tableNames = ActiveProvider.DBHelper.queryTableNames(db);
            assertThat(tableNames.length, is(1));
            assertThat(tableNames[0], is("foo"));
        }

        @Test public void getMissingMigrations() {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(null, null, 0);
            setMigrations(db, "Migration2");

            List<ActiveMigration> migrations = helper.getMissingMigrations(db);
            assertThat(migrations.size(), is(2));
            assertThat(migrations.get(0), is(instanceOf(DataProvider.Migration1.class)));
            assertThat(migrations.get(1), is(instanceOf(DataProvider.Migration3.class)));
        }

        private void setMigrations(SQLiteDatabase db, String... migrations) {
            db.execSQL("DROP TABLE IF EXISTS " + ActiveProvider.MIGRATIONS_TABLE);
            ActiveProvider.DBHelper.createMigrationsTable(db);

            ContentValues values = new ContentValues(1);
            db.beginTransaction();
            try {
                for (String migration : migrations) {
                    values.put("name", migration);
                    db.insert(ActiveProvider.MIGRATIONS_TABLE, null, values);
                }
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }

    }

}
