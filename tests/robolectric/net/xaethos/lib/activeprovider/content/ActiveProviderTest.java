package net.xaethos.lib.activeprovider.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

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

        cursor = provider.query(dirUri, null, null, null, null);
        assertThat(cursor.getColumnCount(), is(12));
        assertThat(Arrays.asList(cursor.getColumnNames()), hasItems(
                "_id",
                "foo",
                "bar",
                "bool",
                "short",
                "int",
                "long",
                "float",
                "double",
                "data",
                "created_at",
                "updated_at"));

        cursor = provider.query(dirUri, ID_PROJECTION, null, null, null);
        assertThat(cursor.getColumnCount(), is(1));
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
		values.put("foo", "Hello");

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
				"foo=?", new String[]{"Hello"}, null);
		assertThat(cursor.getCount(), is(2));
		assertThat(cursorIdIterable(cursor), hasItems(ids));
	}

	@Test public void queryCanSort() {
		long idFooFirst = makeSimpleData("A", "B");
		long idBarFirst = makeSimpleData("B", "A");
		Cursor cursor;

		cursor = provider.query(dirUri, ID_PROJECTION, null, null, "foo ASC");
		cursor.moveToFirst();
		assertThat(cursor.getLong(0), is(idFooFirst));

		cursor = provider.query(dirUri, ID_PROJECTION, null, null, "bar ASC");
		cursor.moveToFirst();
		assertThat(cursor.getLong(0), is(idBarFirst));
	}

	@Test public void canInsert() {
		ContentValues values = new ContentValues();
		values.put("bool", true);
		values.put("data", new byte[]{0,1,2,3});
		values.put("double", 8.8);
		values.put("float", 4.4F);
		values.put("int", 512);
		values.put("long", 1234567890L);
		values.put("short", (short)42);
        values.put("foo", "Hello World!");
        values.putNull("bar");

        Uri uri = provider.insert(ModelManager.getContentUri(Data.class), values);
		assertThat(provider.getType(uri), is(ModelManager.getContentItemType(Data.class)));

		Cursor cursor = provider.query(uri, null, null, null, null);
		assertThat(cursor.moveToFirst(), is(true));
		assertThat(cursor.getInt(cursor.getColumnIndex("bool")), is(not(0)));
		assertThat(cursor.getBlob(cursor.getColumnIndex("data")), is(new byte[]{0,1,2,3}));
		assertThat(cursor.getDouble(cursor.getColumnIndex("double")), is(8.8));
		assertThat(cursor.getFloat(cursor.getColumnIndex("float")), is(4.4F));
		assertThat(cursor.getInt(cursor.getColumnIndex("int")), is(512));
		assertThat(cursor.getLong(cursor.getColumnIndex("long")), is(1234567890L));
		assertThat(cursor.getShort(cursor.getColumnIndex("short")), is((short)42));
		assertThat(cursor.getString(cursor.getColumnIndex("foo")), is("Hello World!"));
		assertThat(cursor.isNull(cursor.getColumnIndex("bar")), is(true));
	}

	@Test(expected=IllegalArgumentException.class)
	public void insertHandlesItemUris() {
		ContentValues values = new ContentValues();
		values.put("foo", "Hello");
		Uri uri = Uri.withAppendedPath(dirUri, "1");
		provider.insert(uri, values);
	}

	@Test public void insertNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put("foo", "Hello");

		resolverShadow.getNotifiedUris().clear();
		provider.insert(dirUri, values);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();
		assertThat(notifiedUris.size(), is(1));
		assertThat(notifiedUris.get(0).uri, is(dirUri));
	}

	@Test public void canUpdate() {
		ContentValues values = new ContentValues();
		values.put("foo", "Hello");

		provider.insert(ModelManager.getContentUri(Data.class), values);

		values.remove("foo");
		values.put("bar", "Goodbye");
		assertThat(provider.update(dirUri, values, null, null), is(1));

		Cursor cursor = provider.query(
				dirUri, new String[]{"foo", "bar"}, null, null, null);
		cursor.moveToFirst();
		assertThat(cursor.getString(0), is("Hello"));
		assertThat(cursor.getString(1), is("Goodbye"));
	}

	@Test public void canUpdateMany() {
		Long[] ids = makeSimpleData("Hello", null, 2);
		makeSimpleData("Hola", null, 2);

		ContentValues values = new ContentValues();
		values.put("foo", "Konnichiwa");
		assertThat(provider.update(
				dirUri, values, "foo=?", new String[]{"Hello"}), is(2));

		Cursor cursor = provider.query(
				dirUri, null,
				"foo=?", new String[]{"Konnichiwa"}, null);
		assertThat(cursor.getCount(), is(2));
		assertThat(cursorIdIterable(cursor), hasItems(ids));
	}

	@Test public void canUpdateById() {
		Long[] ids = makeSimpleData("Hello", null, 2);
		Uri hiUri = ContentUris.withAppendedId(dirUri, ids[0]);
		Uri byeUri = ContentUris.withAppendedId(dirUri, ids[1]);

		ContentValues values = new ContentValues();
		values.put("foo", "Goodbye");
		assertThat(provider.update(byeUri, values, null, null), is(1));

		Cursor cursor;
		cursor = provider.query(hiUri, new String[]{"foo"}, null, null, null);
		cursor.moveToFirst();
		assertThat(cursor.getString(0), is("Hello"));
		cursor = provider.query(byeUri, new String[]{"foo"}, null, null, null);
		cursor.moveToFirst();
		assertThat(cursor.getString(0), is("Goodbye"));
	}

	@Test public void updateNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put("foo", "Hello");
		Uri uri = provider.insert(dirUri, values);

		resolverShadow.getNotifiedUris().clear();
		values.put("foo", "Goodbye");
		provider.update(uri, values, null, null);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();

        assertThat(notifiedUris.size(), is(1));
        assertThat(notifiedUris.get(0).uri, is(uri));
    }

	@Test public void canDelete() {
		ContentValues values = new ContentValues();
		values.put("foo", "Hello");

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
		values.put("foo", "Hello");
		Uri uri = provider.insert(dirUri, values);

		resolverShadow.getNotifiedUris().clear();
		provider.delete(uri, null, null);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();

        assertThat(notifiedUris.size(), is(1));
        assertThat(notifiedUris.get(0).uri, is(uri));
    }

//	@Test public void canInferDatabaseVersionFromMigrationCount() {
//		DataProvider provider = new DataProvider();
//		assertThat(provider.migrations.size(), is(0));
//		assertThat(provider.getDatabaseVersion(), is(1));
//
//		provider.migrations.add(new NullMigration());
//		provider.migrations.add(new NullMigration());
//		assertThat(provider.getDatabaseVersion(), is(3));
//	}
//
//	@Test public void onUpgradeCallsMigrationOnce() {
//		SQLiteOpenHelper dbHelper = provider.getDBHelper();
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		NullMigration mig1to2 = new NullMigration();
//		NullMigration mig2to3 = new NullMigration();
//
//		provider.migrations.add(mig1to2);
//		dbHelper.onUpgrade(db, 1, 2);
//		assertThat(mig1to2.didUpgrade, is(true));
//
//		mig1to2.didUpgrade = false;
//		provider.migrations.add(mig2to3);
//		dbHelper.onUpgrade(db, 2, 3);
//		assertThat(mig1to2.didUpgrade, is(false));
//		assertThat(mig2to3.didUpgrade, is(true));
//	}
//
//	//TODO Migration throws proper exceptions
//	//TODO Migration rolls back on exception

	/////////////// Helpers ///////////////

    public void prepareDatabase() {
        SQLiteDatabase database = provider.mDBHelper.getWritableDatabase();
        database.execSQL("CREATE TABLE data (" +
                "_id INTEGER PRIMARY KEY," +
                "foo TEXT," +
                "bar TEXT," +
                "bool INTEGER," +
                "short INTEGER," +
                "int INTEGER," +
                "long INTEGER," +
                "float REAL," +
                "double REAL," +
                "data BLOB," +
                "created_at INTEGER," +
                "updated_at INTEGER);");
    }

	private long makeSimpleData(ContentValues values) {
		Uri uri = provider.insert(dirUri, values);
		return ContentUris.parseId(uri);
	}

	private long makeSimpleData(String foo, String bar) {
		return makeSimpleData(foo, bar, 1)[0];
	}

	private Long[] makeSimpleData(String foo, String bar, int count) {
		Long[] ids = new Long[count];
		ContentValues values = new ContentValues();
		if (foo!= null) values.put(Data.FOO, foo);
		if (bar!= null) values.put(Data.BAR, bar);
        values.put(Data.CREATED_AT, System.currentTimeMillis());

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

//	class NullMigration extends Migration {
//		public boolean didUpgrade = false;
//		@Override public boolean onUpgrade(SQLiteDatabase db) {
//			didUpgrade = true;
//			return true;
//		}
//	}

}
