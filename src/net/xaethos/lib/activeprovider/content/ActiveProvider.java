package net.xaethos.lib.activeprovider.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class ActiveProvider extends ContentProvider {

	/////////////// Inner classes ///////////////

//	private class DBHelper extends SQLiteOpenHelper {
//
//		public DBHelper(Context context) {
//			super(context, getDatabaseName(), null, getDatabaseVersion());
//		}
//
//		@Override
//		public void onCreate(SQLiteDatabase db) {
//			for (RecordInfo record : getRecordInfo()) {
//				db.execSQL(record.getSQLiteCreateTable());
//			}
//		}
//
//		@Override
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			if (oldVersion > newVersion) {
//				throw new MigrationException("Cannot downgrade database");
//			}
//
//			if (oldVersion < 1) {
//				throw new IllegalArgumentException("Version code must be greater than 0");
//			}
//
//			Migration[] migrations = getMigrations();
//
//			// Version codes are 1-based.
//			for (int i = oldVersion; i < newVersion; ++i) {
//				migrations[i-1].onUpgrade(db);
//			}
//		}
//
//		@SuppressWarnings("unused")
//		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			throw new MigrationException("Cannot downgrade database");
//		}
//
//	}
//
//	/////////////// Static methods ///////////////
//
//	private static boolean isItemUri(int match) {
//		return (match % 2) == 1;
//	}
//
//	private static String prependIdConstraint(Uri uri, String select) {
//		String idConstraint = BaseColumns._ID + "=" + uri.getLastPathSegment();
//		if (TextUtils.isEmpty(select)) {
//			select = idConstraint;
//		}
//		else {
//			select = idConstraint + " AND (" + select + ")";
//		}
//		return select;
//	}
//
//	/////////////// Instance fields ///////////////
//
//	protected DBHelper mDBHelper;
//	protected UriMatcher mUriMatcher;
//
//	/////////////// Instance methods ///////////////
//
//	///// Abstract methods
//
//	protected abstract String getDatabaseName();
//
//	protected abstract RecordInfo[] getRecordInfo();
//
//	protected abstract Migration[] getMigrations();
//
//	///// Database info
//
//	public int getDatabaseVersion() {
//		Migration[] migrations = getMigrations();
//		if (migrations == null) return 1;
//		return migrations.length + 1;
//	}

	///// ContentProvider implementation

	@Override
	public boolean onCreate() {
//		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//
//		int i = 0;
//		String authority;
//		String table;
//		for (RecordInfo record : getRecordInfo()) {
//			authority = record.getAuthority();
//			table = record.getTableName();
//			mUriMatcher.addURI(authority, table, i++);
//			mUriMatcher.addURI(authority, table + "/#", i++);
//		}
//
//		mDBHelper = new DBHelper(getContext());
		return true;
    }

	@Override
	public String getType(Uri uri) {
//		int match = mUriMatcher.match(uri);
//		RecordInfo record = recordInfoFromUriMatch(match);
//
//		if (isItemUri(match)) return record.getContentItemType();
//		return record.getContentType();
        return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
//		int match = mUriMatcher.match(uri);
//		RecordInfo record = recordInfoFromUriMatch(match);
//
//		if (isItemUri(match)) {
//			selection = prependIdConstraint(uri, selection);
//		}
//
//		// Project columns
//		HashMap<String,String> projectionMap = record.getProjectionMap();
//		String[] columns = null;
//		if (projection != null) {
//			columns = new String[projection.length];
//			for (int i=0; i<projection.length; ++i) {
//				columns[i] = projectionMap.get(projection[i]);
//			}
//		}
//
//		SQLiteDatabase db = mDBHelper.getReadableDatabase();
//		Cursor cursor = db.query(
//				record.getTableName(),
//				columns,
//				selection,
//				selectionArgs,
//				null,
//				null,
//				sortOrder);
//		cursor.setNotificationUri(getContext().getContentResolver(), record.getContentUri());
//		return cursor;
        return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
//		int match = mUriMatcher.match(uri);
//
//		if (isItemUri(match)) {
//			throw new IllegalArgumentException("Cannot insert to item URI: " + uri);
//		}
//
//		RecordInfo record = recordInfoFromUriMatch(match);
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//
//		long id = db.insert(record.getTableName(), null, values);
//		if (id < 0) return null;
//
//		getContext().getContentResolver().notifyChange(uri, null);
//		return ContentUris.withAppendedId(record.getContentUri(), id);
        return null;
	}

	@Override
	public int update(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
//		RecordInfo record = recordInfoFromUriMatch(mUriMatcher.match(uri));
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//
//		if (isItemUri(mUriMatcher.match(uri))) {
//			selection = prependIdConstraint(uri, selection);
//		}
//
//		int count = db.update(record.getTableName(), values, selection, selectionArgs);
//		if (count > 0) {
//			getContext().getContentResolver().notifyChange(uri, null);
//		}
//		return count;
        return -1;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
//		RecordInfo record = recordInfoFromUriMatch(mUriMatcher.match(uri));
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//
//		if (isItemUri(mUriMatcher.match(uri))) {
//			selection = prependIdConstraint(uri, selection);
//		}
//
//		int count = db.delete(record.getTableName(), selection, selectionArgs);
//		if (count > 0) {
//			getContext().getContentResolver().notifyChange(uri, null);
//		}
//		return count;
        return -1;
	}

	///// Helper methods

//	private RecordInfo recordInfoFromUriMatch(int match) {
//		RecordInfo[] records = getRecordInfo();
//		int index = match / 2;
//		if (match < 0 || index >= records.length) {
//			throw new IllegalArgumentException("Invalid match: " + match);
//		}
//		return records[index];
//	}

}
