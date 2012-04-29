package net.xaethos.lib.activeprovider.content;

import android.content.ContentUris;
import android.net.Uri;
import com.example.fixtures.Data;
import com.example.fixtures.DataProvider;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.util.DatabaseConfig.UsingDatabaseMap;
import com.xtremelabs.robolectric.util.SQLiteMap;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@UsingDatabaseMap(SQLiteMap.class)
@RunWith(RobolectricTestRunner.class)
public class ActiveProviderTest {

//	static final String[] ID_PROJECTION = { BaseColumns._ID };

	DataProvider provider;

//	ContentResolver resolver;
//	ShadowContentResolver resolverShadow;

	/////////////// Set up ///////////////

	@Before public void getProvider() {
		provider = new DataProvider();
	}

//	@Before public void getResolver() {
//		resolver = Robolectric.application.getContentResolver();
//		resolverShadow = Robolectric.shadowOf(resolver);
//	}

	/////////////// Tests ///////////////

	@Test public void canCreate() {
		assertThat(provider.onCreate(), is(true));
	}

    @Test public void canGetProviderInfo() {
        assertThat(provider.getProviderInfo(),
                is(DataProvider.class.getAnnotation(Provider.class)));
    }

    @Test public void canGetModels() {
        Model[] models = provider.getModels();
        assertThat(models.length, is(1));
        assertThat(models[0],
                is(provider.getProviderInfo().models()[0].getAnnotation(Model.class)));
    }

	@Test public void canMatchUris() {
        provider.onCreate();

        Model annotation = Data.class.getAnnotation(Model.class);
        String authority = annotation.authority();
        String tableName = annotation.tableName();
        String type      = annotation.contentType();

        Uri dirUri = Uri.parse("content://" + authority + "/" + tableName);
        assertThat(provider.getType(dirUri),
                is("vnd.android.cursor.dir/" + type));

        Uri itemUri = Uri.parse("content://" + authority + "/" + tableName + "/1");
        assertThat(provider.getType(itemUri),
				is("vnd.android.cursor.item/" + type));
	}

//	@Test public void queryHandlesProjections() {
//		Cursor cursor;
//
//		cursor = (SQLiteCursor) provider.query(dataUri, null, null, null, null);
//		assertThat(cursor.getColumnCount(), is(3));
//		assertThat(Arrays.asList(cursor.getColumnNames()), hasItems(new String[]{"_id", "foo", "bar"}));
//
//		cursor = (SQLiteCursor) provider.query(dataUri, ID_PROJECTION, null, null, null);
//		assertThat(cursor.getColumnCount(), is(1));
//		assertThat(Arrays.asList(cursor.getColumnNames()), hasItems(ID_PROJECTION));
//	}
//
//	@Test public void querySetsNotificationUri() {
//		SQLiteCursor cursor =
//				(SQLiteCursor) provider.query(dataUri, null, null, null, null);
//		ShadowSQLiteCursor shadow = Robolectric.shadowOf(cursor);
//		assertThat(shadow.getNotificationUri_Compatibility(), is(dataUri));
//	}
//
//	@Test public void queryHandlesDirAndItemUris() {
//		Cursor cursor;
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		Uri itemUri = provider.insert(dataUri, values);
//		provider.insert(dataUri, values);
//
//		cursor = (SQLiteCursor) provider.query(dataUri, ID_PROJECTION, null, null, null);
//		assertThat(cursor.getCount(), is(2));
//
//		cursor = (SQLiteCursor) provider.query(itemUri, ID_PROJECTION, null, null, null);
//		assertThat(cursor.getCount(), is(1));
//		cursor.moveToFirst();
//		assertThat(cursor.getLong(0), is(ContentUris.parseId(itemUri)));
//	}
//
//	@Test public void queryCanFilter() {
//		Long[] ids = makeSimpleData("Hello", null, 2);
//		makeSimpleData("Goodbye", null, 2);
//
//		Cursor cursor = provider.query(
//				dataUri, null,
//				"foo=?", new String[]{"Hello"}, null);
//		assertThat(cursor.getCount(), is(2));
//		assertThat(cursorIdIterable(cursor), hasItems(ids));
//	}
//
//	@Test public void queryCanSort() {
//		long idFooFirst = makeSimpleData("A", "B");
//		long idBarFirst = makeSimpleData("B", "A");
//		Cursor cursor;
//
//		cursor = provider.query(dataUri, ID_PROJECTION, null, null, "foo ASC");
//		cursor.moveToFirst();
//		assertThat(cursor.getLong(0), is(idFooFirst));
//
//		cursor = provider.query(dataUri, ID_PROJECTION, null, null, "bar ASC");
//		cursor.moveToFirst();
//		assertThat(cursor.getLong(0), is(idBarFirst));
//	}
//
//	@Test public void canInsert() {
//		ContentValues values = new ContentValues();
//		values.put("pBoolean", true);
//		values.put("pByte", (byte)16);
//		values.put("pBlob", new byte[]{0,1,2,3});
//		values.put("pDouble", 8.8);
//		values.put("pFloat", 4.4F);
//		values.put("pInteger", 512);
//		values.put("pLong", 1234567890L);
//		values.put("pShort", (short)42);
//		values.put("pString", "Hello World!");
//
//		Uri uri = provider.insert(mConcreteRecord.getContentUri(), values);
//		assertThat(provider.getType(uri), is(mConcreteRecord.getContentItemType()));
//
//		Cursor cursor = provider.query(uri, null, null, null, null);
//		assertThat(cursor.moveToFirst(), is(true));
//		assertThat(cursor.getInt(cursor.getColumnIndex("pBoolean")), is(not(0)));
//		assertThat((byte)cursor.getShort(cursor.getColumnIndex("pByte")), is((byte)16));
//		assertThat(cursor.getBlob(cursor.getColumnIndex("pBlob")), is(new byte[]{0,1,2,3}));
//		assertThat(cursor.getDouble(cursor.getColumnIndex("pDouble")), is(8.8));
//		assertThat(cursor.getFloat(cursor.getColumnIndex("pFloat")), is(4.4F));
//		assertThat(cursor.getInt(cursor.getColumnIndex("pInteger")), is(512));
//		assertThat(cursor.getLong(cursor.getColumnIndex("pLong")), is(1234567890L));
//		assertThat(cursor.getShort(cursor.getColumnIndex("pShort")), is((short)42));
//		assertThat(cursor.getString(cursor.getColumnIndex("pString")), is("Hello World!"));
//		assertThat(cursor.isNull(cursor.getColumnIndex("pParent")), is(true));
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void insertHandlesItemUris() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//		Uri uri = Uri.withAppendedPath(dataUri, "1");
//		provider.insert(uri, values);
//	}
//
//	@Test public void insertNotifiesUri() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		resolverShadow.getNotifiedUris().clear();
//		provider.insert(dataUri, values);
//		assertThat(resolverShadow.getNotifiedUris().size(), is(1));
//		assertThat(resolverShadow.getNotifiedUris(), hasItems(dataUri));
//	}
//
//	@Test public void canUpdate() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		provider.insert(mSimpleRecord.getContentUri(), values);
//
//		values.remove("foo");
//		values.put("bar", "Goodbye");
//		assertThat(provider.update(dataUri, values, null, null), is(1));
//
//		Cursor cursor = provider.query(
//				dataUri, new String[]{"foo", "bar"}, null, null, null);
//		cursor.moveToFirst();
//		assertThat(cursor.getString(0), is("Hello"));
//		assertThat(cursor.getString(1), is("Goodbye"));
//	}
//
//	@Test public void canUpdateMany() {
//		Long[] ids = makeSimpleData("Hello", null, 2);
//		makeSimpleData("Hola", null, 2);
//
//		ContentValues values = new ContentValues();
//		values.put("foo", "Konnichiwa");
//		assertThat(provider.update(
//				dataUri, values, "foo=?", new String[]{"Hello"}), is(2));
//
//		Cursor cursor = provider.query(
//				dataUri, null,
//				"foo=?", new String[]{"Konnichiwa"}, null);
//		assertThat(cursor.getCount(), is(2));
//		assertThat(cursorIdIterable(cursor), hasItems(ids));
//	}
//
//	@Test public void canUpdateById() {
//		Long[] ids = makeSimpleData("Hello", null, 2);
//		Uri hiUri = ContentUris.withAppendedId(dataUri, ids[0]);
//		Uri byeUri = ContentUris.withAppendedId(dataUri, ids[1]);
//
//		ContentValues values = new ContentValues();
//		values.put("foo", "Goodbye");
//		assertThat(provider.update(byeUri, values, null, null), is(1));
//
//		Cursor cursor;
//		cursor = provider.query(hiUri, new String[]{"foo"}, null, null, null);
//		cursor.moveToFirst();
//		assertThat(cursor.getString(0), is("Hello"));
//		cursor = provider.query(byeUri, new String[]{"foo"}, null, null, null);
//		cursor.moveToFirst();
//		assertThat(cursor.getString(0), is("Goodbye"));
//	}
//
//	@Test public void updateNotifiesUri() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//		Uri uri = provider.insert(dataUri, values);
//
//		resolverShadow.getNotifiedUris().clear();
//		values.put("foo", "Goodbye");
//		provider.update(uri, values, null, null);
//		assertThat(resolverShadow.getNotifiedUris().size(), is(1));
//		assertThat(resolverShadow.getNotifiedUris(), hasItems(uri));
//	}
//
//	@Test public void canDelete() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		provider.insert(dataUri, values);
//
//		assertThat(provider.delete(dataUri, null, null), is(1));
//
//		Cursor cursor = provider.query(dataUri, null, null, null, null);
//		assertThat(cursor.getCount(), is(0));
//	}
//
//	@Test public void canDeleteById() {
//		Long[] ids = makeSimpleData("Hello", null, 2);
//		Uri delUri = ContentUris.withAppendedId(dataUri, ids[0]);
//
//		assertThat(provider.delete(delUri, null, null), is(1));
//
//		Cursor cursor;
//		cursor = provider.query(dataUri, new String[]{"_id"}, null, null, null);
//		assertThat(cursor.getCount(), is(1));
//		cursor.moveToFirst();
//		assertThat(cursor.getLong(0), is(ids[1]));
//	}
//
//	@Test public void deleteNotifiesUri() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//		Uri uri = provider.insert(dataUri, values);
//
//		resolverShadow.getNotifiedUris().clear();
//		provider.delete(uri, null, null);
//		assertThat(resolverShadow.getNotifiedUris().size(), is(1));
//		assertThat(resolverShadow.getNotifiedUris(), hasItems(uri));
//	}
//
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
//
//	/////////////// Helpers ///////////////
//
//	private long newSimpleData(ContentValues values) {
//		Uri uri = provider.insert(dataUri, values);
//		return ContentUris.parseId(uri);
//	}
//
//	private long makeSimpleData(String foo, String bar) {
//		return makeSimpleData(foo, bar, 1)[0];
//	}
//
//	private Long[] makeSimpleData(String foo, String bar, int count) {
//		Long[] ids = new Long[count];
//		ContentValues values = new ContentValues();
//		if (foo!= null) values.put("foo", foo);
//		if (bar!= null) values.put("bar", bar);
//
//		for (int i=0; i<count; ++i) {
//			ids[i] = newSimpleData(values);
//		}
//
//		return ids;
//	}
//
//	private Iterable<Long> cursorIdIterable(final Cursor cursor) {
//		cursor.moveToPosition(0);
//		final int idColumn = cursor.getColumnIndex("_id");
//		return new Iterable<Long>() {
//			@Override public Iterator<Long> iterator() {
//				return new Iterator<Long>() {
//					@Override public void remove() {
//						throw new UnsupportedOperationException();
//					}
//
//					@Override public Long next() {
//						if (cursor.isAfterLast()) {
//							throw new NoSuchElementException();
//						}
//						long id = cursor.getLong(idColumn);
//						cursor.moveToNext();
//						return id;
//					}
//
//					@Override
//					public boolean hasNext() {
//						return !cursor.isAfterLast();
//					}
//				};
//			}
//		};
//	}
//
//	class NullMigration extends Migration {
//		public boolean didUpgrade = false;
//		@Override public boolean onUpgrade(SQLiteDatabase db) {
//			didUpgrade = true;
//			return true;
//		}
//	}

}
