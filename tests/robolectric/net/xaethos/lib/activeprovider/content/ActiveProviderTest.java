package net.xaethos.lib.activeprovider.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import com.example.fixtures.Data;
import com.example.fixtures.DataProvider;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContentResolver;
import com.xtremelabs.robolectric.shadows.ShadowSQLiteCursor;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.models.ModelManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class ActiveProviderTest {

	static final String[] ID_PROJECTION = { BaseColumns._ID };

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
        prepareDatabase();
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

	@Test public void canCreate() {
		assertThat(new DataProvider().onCreate(), is(true));
	}

    @Test public void canGetProviderInfo() {
        assertThat(provider.getProviderInfo(),
                is(DataProvider.class.getAnnotation(ProviderInfo.class)));
    }

    @Test public void canGetModels() {
        ModelInfo[] models = provider.getModels();
        assertThat(models.length, is(1));
        assertThat(models[0],
                is(provider.getProviderInfo().models()[0].getAnnotation(ModelInfo.class)));
    }

	@Test public void canMatchUris() {
        String type = dataInfo.contentType();

        assertThat(provider.getType(dirUri), is("vnd.android.cursor.dir/" + type));
        assertThat(provider.getType(itemUri), is("vnd.android.cursor.item/" + type));
	}

    @Test public void queryHandlesProjections() throws Exception {
        Cursor cursor;
        String[] projection = {
                Data._ID,
                Data.STRING,
                Data.BOOL,
                Data.BYTE,
                Data.SHORT,
                Data.INT,
                Data.LONG,
                Data.FLOAT,
                Data.DOUBLE,
                Data.DATA,
                Data.DATE,
                Data._CREATED_AT,
                Data._UPDATED_AT
        };

        cursor = provider.query(dirUri, null, null, null, null);
        assertThat(cursor.getColumnCount(), is(projection.length));
        assertThat(Arrays.asList(cursor.getColumnNames()), hasItems(projection));

        cursor = provider.query(dirUri, ID_PROJECTION, null, null, null);
        assertThat(cursor.getColumnCount(), is(ID_PROJECTION.length));
        assertThat(Arrays.asList(cursor.getColumnNames()), hasItems(ID_PROJECTION));
    }

	@Test public void querySetsNotificationUri() {
		SQLiteCursor cursor =
				(SQLiteCursor) provider.query(dirUri, null, null, null, null);
		ShadowSQLiteCursor shadow = shadowOf(cursor);
		assertThat(shadow.getNotificationUri_Compatibility(), is(dirUri));
	}

	@Test public void queryHandlesDirAndItemUris() {
		Cursor cursor;
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");

		Uri itemUri = provider.insert(dirUri, values);
		provider.insert(dirUri, values);

		cursor = provider.query(dirUri, ID_PROJECTION, null, null, null);
		assertThat(cursor.getCount(), is(2));

		cursor = provider.query(itemUri, ID_PROJECTION, null, null, null);
		assertThat(cursor.getCount(), is(1));
		cursor.moveToFirst();
		assertThat(cursor.getLong(0), is(ContentUris.parseId(itemUri)));
	}

	@Test public void queryCanFilter() {
		Long[] ids = makeSimpleData("Hello", null, 2);
		makeSimpleData("Goodbye", null, 2);

		Cursor cursor = provider.query(
                dirUri, null,
				Data.STRING + "=?", new String[]{"Hello"}, null);
		assertThat(cursor.getCount(), is(2));
		assertThat(cursorIdIterable(cursor), hasItems(ids));
	}

	@Test public void queryCanSort() {
		long idAFirst = makeSimpleData("A", 2);
		long idBFirst = makeSimpleData("B", 1);
		Cursor cursor;

		cursor = provider.query(dirUri, ID_PROJECTION, null, null, Data.STRING + " ASC");
		cursor.moveToFirst();
		assertThat(cursor.getLong(0), is(idAFirst));

		cursor = provider.query(dirUri, ID_PROJECTION, null, null, Data.INT + " ASC");
		cursor.moveToFirst();
		assertThat(cursor.getLong(0), is(idBFirst));
	}

	@Test public void canInsert() {
		ContentValues values = new ContentValues();
        values.put(Data.STRING, "Hello World!");
        values.put(Data.BOOL, true);
        values.put(Data.BYTE, (byte)7);
        values.put(Data.SHORT, (short)42);
        values.put(Data.INT, 512);
        values.put(Data.LONG, 1234567890L);
        values.put(Data.FLOAT, 4.4F);
        values.put(Data.DOUBLE, 8.8);
        values.put(Data.DATA, new byte[]{0,1,2,3});
        values.putNull(Data.DATE);

        Uri uri = provider.insert(ModelManager.getContentUri(Data.class), values);
		assertThat(provider.getType(uri), is(ModelManager.getContentItemType(Data.class)));

		Cursor cursor = provider.query(uri, null, null, null, null);
		assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndex(Data.STRING)), is("Hello World!"));
        assertThat(cursor.getInt(cursor.getColumnIndex(Data.BOOL)), is(not(0)));
        assertThat((byte)cursor.getShort(cursor.getColumnIndex(Data.BYTE)), is((byte)7));
        assertThat(cursor.getShort(cursor.getColumnIndex(Data.SHORT)), is((short)42));
        assertThat(cursor.getInt(cursor.getColumnIndex(Data.INT)), is(512));
        assertThat(cursor.getLong(cursor.getColumnIndex(Data.LONG)), is(1234567890L));
		assertThat(cursor.getFloat(cursor.getColumnIndex(Data.FLOAT)), is(4.4F));
        assertThat(cursor.getDouble(cursor.getColumnIndex(Data.DOUBLE)), is(8.8));
        assertThat(cursor.getBlob(cursor.getColumnIndex(Data.DATA)), is(new byte[]{0,1,2,3}));
        assertThat(cursor.isNull(cursor.getColumnIndex(Data.DATE)), is(true));
	}

	@Test(expected=IllegalArgumentException.class)
	public void insertHandlesItemUris() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");
		Uri uri = Uri.withAppendedPath(dirUri, "1");
		provider.insert(uri, values);
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

	@Test public void canUpdate() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");

		provider.insert(ModelManager.getContentUri(Data.class), values);

		values.remove(Data.STRING);
		values.put(Data.INT, 42);
		assertThat(provider.update(dirUri, values, null, null), is(1));

		Cursor cursor = provider.query(
				dirUri, new String[]{Data.STRING, Data.INT}, null, null, null);
		cursor.moveToFirst();
		assertThat(cursor.getString(0), is("Hello"));
		assertThat(cursor.getInt(1), is(42));
	}

	@Test public void canUpdateMany() {
		Long[] ids = makeSimpleData("Hello", null, 2);
		makeSimpleData("Hola", null, 2);

		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Konnichiwa");
		assertThat(provider.update(
				dirUri, values, Data.STRING + "=?", new String[]{"Hello"}), is(2));

		Cursor cursor = provider.query(
				dirUri, null,
                Data.STRING + "=?", new String[]{"Konnichiwa"}, null);
		assertThat(cursor.getCount(), is(2));
		assertThat(cursorIdIterable(cursor), hasItems(ids));
	}

	@Test public void canUpdateById() {
		Long[] ids = makeSimpleData("Hello", null, 2);
		Uri hiUri = ContentUris.withAppendedId(dirUri, ids[0]);
		Uri byeUri = ContentUris.withAppendedId(dirUri, ids[1]);

		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Goodbye");
		assertThat(provider.update(byeUri, values, null, null), is(1));

		Cursor cursor;
		cursor = provider.query(hiUri, new String[]{Data.STRING}, null, null, null);
		cursor.moveToFirst();
		assertThat(cursor.getString(0), is("Hello"));
		cursor = provider.query(byeUri, new String[]{Data.STRING}, null, null, null);
		cursor.moveToFirst();
		assertThat(cursor.getString(0), is("Goodbye"));
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

	@Test public void canDelete() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");

		provider.insert(dirUri, values);

		assertThat(provider.delete(dirUri, null, null), is(1));

		Cursor cursor = provider.query(dirUri, null, null, null, null);
		assertThat(cursor.getCount(), is(0));
	}

	@Test public void canDeleteById() {
		Long[] ids = makeSimpleData("Hello", null, 2);
		Uri delUri = ContentUris.withAppendedId(dirUri, ids[0]);

		assertThat(provider.delete(delUri, null, null), is(1));

		Cursor cursor;
		cursor = provider.query(dirUri, new String[]{"_id"}, null, null, null);
		assertThat(cursor.getCount(), is(1));
		cursor.moveToFirst();
		assertThat(cursor.getLong(0), is(ids[1]));
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

    public void prepareDatabase() {
        SQLiteDatabase database = provider.mDBHelper.getWritableDatabase();
        database.execSQL("CREATE TABLE data (" +
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
    }

	private long makeSimpleData(ContentValues values) {
		Uri uri = provider.insert(dirUri, values);
		return ContentUris.parseId(uri);
	}

	private long makeSimpleData(String s, Integer n) {
		return makeSimpleData(s, n, 1)[0];
	}

	private Long[] makeSimpleData(String s, Integer n, int count) {
		Long[] ids = new Long[count];
		ContentValues values = new ContentValues();
		if (s!= null) values.put(Data.STRING, s);
		if (n!= null) values.put(Data.INT, n);
        long now = System.currentTimeMillis();
        values.put(Data._CREATED_AT, now);
        values.put(Data._UPDATED_AT, now);

		for (int i=0; i<count; ++i) {
			ids[i] = makeSimpleData(values);
		}

		return ids;
	}

	private Iterable<Long> cursorIdIterable(final Cursor cursor) {
		cursor.moveToPosition(0);
		final int idColumn = cursor.getColumnIndex(BaseColumns._ID);
		return new Iterable<Long>() {
			@Override public Iterator<Long> iterator() {
				return new Iterator<Long>() {
					@Override public void remove() {
						throw new UnsupportedOperationException();
					}

					@Override public Long next() {
						if (cursor.isAfterLast()) {
							throw new NoSuchElementException();
						}
						long id = cursor.getLong(idColumn);
						cursor.moveToNext();
						return id;
					}

					@Override
					public boolean hasNext() {
						return !cursor.isAfterLast();
					}
				};
			}
		};
	}

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
            helper.onCreate(db);
            db.execSQL("CREATE TABLE foo (bar);");

            assertNull(helper.getTableNames());
            helper.onOpen(db);

            String[] tableNames = helper.getTableNames();
            assertThat(tableNames.length, is(2));
            assertThat(Arrays.asList(tableNames),
                    hasItems("foo", ActiveProvider.MIGRATIONS_TABLE));
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
