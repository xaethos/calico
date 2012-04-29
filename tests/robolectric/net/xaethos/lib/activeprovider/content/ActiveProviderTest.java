package net.xaethos.lib.activeprovider.content;

import com.example.fixtures.DataProvider;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.util.DatabaseConfig.UsingDatabaseMap;
import com.xtremelabs.robolectric.util.SQLiteMap;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@UsingDatabaseMap(SQLiteMap.class)
@RunWith(RobolectricTestRunner.class)
public class ActiveProviderTest {

//	static final String[] ID_PROJECTION = { BaseColumns._ID };

//	DataProvider mProvider;

//	ContentResolver mResolver;
//	ShadowContentResolver mResolverShadow;

//	RecordInfo mSimpleRecord;
//	RecordInfo mConcreteRecord;

//	Uri mSimpleUri;

	/////////////// Set up ///////////////

//	@Before public void getProvider() {
//		mProvider = new DataProvider();
//		mProvider.onCreate();
//	}

//	@Before public void getResolver() {
//		mResolver = Robolectric.application.getContentResolver();
//		mResolverShadow = Robolectric.shadowOf(mResolver);
//	}

//	@Before public void getRecords() {
//		mSimpleRecord = new RecordInfo(SimpleData.class);
//		mConcreteRecord = new RecordInfo(ConcreteData.class);
//
//		mSimpleUri = mSimpleRecord.getContentUri();
//	}

	/////////////// Tests ///////////////

	@Test public void canCreate() {
		assertThat(new DataProvider().onCreate(), is(true));
	}

//	@Test public void canMatchUris() {
//		assertThat(mProvider.getType(mConcreteRecord.getContentUri()),
//				is(mConcreteRecord.getContentType()));
//		assertThat(mProvider.getType(ContentUris.withAppendedId(mConcreteRecord.getContentUri(), 1)),
//				is(mConcreteRecord.getContentItemType()));
//
//		assertThat(mProvider.getType(mSimpleUri),
//				is(mSimpleRecord.getContentType()));
//		assertThat(mProvider.getType(ContentUris.withAppendedId(mSimpleUri, 1)),
//				is(mSimpleRecord.getContentItemType()));
//	}
//
//	@Test public void queryHandlesProjections() {
//		Cursor cursor;
//
//		cursor = (SQLiteCursor) mProvider.query(mSimpleUri, null, null, null, null);
//		assertThat(cursor.getColumnCount(), is(3));
//		assertThat(Arrays.asList(cursor.getColumnNames()), hasItems(new String[]{"_id", "foo", "bar"}));
//
//		cursor = (SQLiteCursor) mProvider.query(mSimpleUri, ID_PROJECTION, null, null, null);
//		assertThat(cursor.getColumnCount(), is(1));
//		assertThat(Arrays.asList(cursor.getColumnNames()), hasItems(ID_PROJECTION));
//	}
//
//	@Test public void querySetsNotificationUri() {
//		SQLiteCursor cursor =
//				(SQLiteCursor) mProvider.query(mSimpleUri, null, null, null, null);
//		ShadowSQLiteCursor shadow = Robolectric.shadowOf(cursor);
//		assertThat(shadow.getNotificationUri_Compatibility(), is(mSimpleUri));
//	}
//
//	@Test public void queryHandlesDirAndItemUris() {
//		Cursor cursor;
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		Uri itemUri = mProvider.insert(mSimpleUri, values);
//		mProvider.insert(mSimpleUri, values);
//
//		cursor = (SQLiteCursor) mProvider.query(mSimpleUri, ID_PROJECTION, null, null, null);
//		assertThat(cursor.getCount(), is(2));
//
//		cursor = (SQLiteCursor) mProvider.query(itemUri, ID_PROJECTION, null, null, null);
//		assertThat(cursor.getCount(), is(1));
//		cursor.moveToFirst();
//		assertThat(cursor.getLong(0), is(ContentUris.parseId(itemUri)));
//	}
//
//	@Test public void queryCanFilter() {
//		Long[] ids = makeSimpleData("Hello", null, 2);
//		makeSimpleData("Goodbye", null, 2);
//
//		Cursor cursor = mProvider.query(
//				mSimpleUri, null,
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
//		cursor = mProvider.query(mSimpleUri, ID_PROJECTION, null, null, "foo ASC");
//		cursor.moveToFirst();
//		assertThat(cursor.getLong(0), is(idFooFirst));
//
//		cursor = mProvider.query(mSimpleUri, ID_PROJECTION, null, null, "bar ASC");
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
//		Uri uri = mProvider.insert(mConcreteRecord.getContentUri(), values);
//		assertThat(mProvider.getType(uri), is(mConcreteRecord.getContentItemType()));
//
//		Cursor cursor = mProvider.query(uri, null, null, null, null);
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
//		Uri uri = Uri.withAppendedPath(mSimpleUri, "1");
//		mProvider.insert(uri, values);
//	}
//
//	@Test public void insertNotifiesUri() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		mResolverShadow.getNotifiedUris().clear();
//		mProvider.insert(mSimpleUri, values);
//		assertThat(mResolverShadow.getNotifiedUris().size(), is(1));
//		assertThat(mResolverShadow.getNotifiedUris(), hasItems(mSimpleUri));
//	}
//
//	@Test public void canUpdate() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		mProvider.insert(mSimpleRecord.getContentUri(), values);
//
//		values.remove("foo");
//		values.put("bar", "Goodbye");
//		assertThat(mProvider.update(mSimpleUri, values, null, null), is(1));
//
//		Cursor cursor = mProvider.query(
//				mSimpleUri, new String[]{"foo", "bar"}, null, null, null);
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
//		assertThat(mProvider.update(
//				mSimpleUri, values, "foo=?", new String[]{"Hello"}), is(2));
//
//		Cursor cursor = mProvider.query(
//				mSimpleUri, null,
//				"foo=?", new String[]{"Konnichiwa"}, null);
//		assertThat(cursor.getCount(), is(2));
//		assertThat(cursorIdIterable(cursor), hasItems(ids));
//	}
//
//	@Test public void canUpdateById() {
//		Long[] ids = makeSimpleData("Hello", null, 2);
//		Uri hiUri = ContentUris.withAppendedId(mSimpleUri, ids[0]);
//		Uri byeUri = ContentUris.withAppendedId(mSimpleUri, ids[1]);
//
//		ContentValues values = new ContentValues();
//		values.put("foo", "Goodbye");
//		assertThat(mProvider.update(byeUri, values, null, null), is(1));
//
//		Cursor cursor;
//		cursor = mProvider.query(hiUri, new String[]{"foo"}, null, null, null);
//		cursor.moveToFirst();
//		assertThat(cursor.getString(0), is("Hello"));
//		cursor = mProvider.query(byeUri, new String[]{"foo"}, null, null, null);
//		cursor.moveToFirst();
//		assertThat(cursor.getString(0), is("Goodbye"));
//	}
//
//	@Test public void updateNotifiesUri() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//		Uri uri = mProvider.insert(mSimpleUri, values);
//
//		mResolverShadow.getNotifiedUris().clear();
//		values.put("foo", "Goodbye");
//		mProvider.update(uri, values, null, null);
//		assertThat(mResolverShadow.getNotifiedUris().size(), is(1));
//		assertThat(mResolverShadow.getNotifiedUris(), hasItems(uri));
//	}
//
//	@Test public void canDelete() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//
//		mProvider.insert(mSimpleUri, values);
//
//		assertThat(mProvider.delete(mSimpleUri, null, null), is(1));
//
//		Cursor cursor = mProvider.query(mSimpleUri, null, null, null, null);
//		assertThat(cursor.getCount(), is(0));
//	}
//
//	@Test public void canDeleteById() {
//		Long[] ids = makeSimpleData("Hello", null, 2);
//		Uri delUri = ContentUris.withAppendedId(mSimpleUri, ids[0]);
//
//		assertThat(mProvider.delete(delUri, null, null), is(1));
//
//		Cursor cursor;
//		cursor = mProvider.query(mSimpleUri, new String[]{"_id"}, null, null, null);
//		assertThat(cursor.getCount(), is(1));
//		cursor.moveToFirst();
//		assertThat(cursor.getLong(0), is(ids[1]));
//	}
//
//	@Test public void deleteNotifiesUri() {
//		ContentValues values = new ContentValues();
//		values.put("foo", "Hello");
//		Uri uri = mProvider.insert(mSimpleUri, values);
//
//		mResolverShadow.getNotifiedUris().clear();
//		mProvider.delete(uri, null, null);
//		assertThat(mResolverShadow.getNotifiedUris().size(), is(1));
//		assertThat(mResolverShadow.getNotifiedUris(), hasItems(uri));
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
//		SQLiteOpenHelper dbHelper = mProvider.getDBHelper();
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		NullMigration mig1to2 = new NullMigration();
//		NullMigration mig2to3 = new NullMigration();
//
//		mProvider.migrations.add(mig1to2);
//		dbHelper.onUpgrade(db, 1, 2);
//		assertThat(mig1to2.didUpgrade, is(true));
//
//		mig1to2.didUpgrade = false;
//		mProvider.migrations.add(mig2to3);
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
//		Uri uri = mProvider.insert(mSimpleUri, values);
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
