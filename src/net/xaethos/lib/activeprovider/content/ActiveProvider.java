package net.xaethos.lib.activeprovider.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Provider;

import java.util.HashMap;

public abstract class ActiveProvider extends ContentProvider {

	/////////////// Constants ///////////////

    private static final String MIME_BASE_ITEM = "vnd.android.cursor.item/";
    private static final String MIME_BASE_DIR  = "vnd.android.cursor.dir/";

    /////////////// Inner classes ///////////////

	public class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, getDatabaseName(), null, getDatabaseVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
//			for (RecordInfo record : getModels()) {
//				db.execSQL(record.getSQLiteCreateTable());
//			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
		}

		@SuppressWarnings("unused")
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			throw new UnsupportedOperationException("ActiveProvider cannot downgrade database");
		}

	}

	/////////////// Static methods ///////////////

	private static boolean isItemUri(int match) {
		return (match % 2) == 1;
	}

	private static String prependIdConstraint(Uri uri, String select) {
		String idConstraint = BaseColumns._ID + "=" + uri.getLastPathSegment();
		if (TextUtils.isEmpty(select)) {
			select = idConstraint;
		}
		else {
			select = idConstraint + " AND (" + select + ")";
		}
		return select;
	}

	/////////////// Instance fields ///////////////

    private Provider mInfo;
    private Model[] mModels;

    protected DBHelper mDBHelper;
	protected UriMatcher mUriMatcher;
    protected HashMap<Model,HashMap<String,String>> mProjectionMaps;

	/////////////// Instance methods ///////////////

	///// Abstract methods

	protected abstract String getDatabaseName();

//	protected abstract Migration[] getMigrations();

	///// Provider info

	public int getDatabaseVersion() {
//		Migration[] migrations = getMigrations();
//		if (migrations == null) return 1;
//		return migrations.length + 1;
        return 1;
	}

    public Provider getProviderInfo() {
        if (mInfo == null) {
            Class<? extends ActiveProvider> cls = this.getClass();
            if (!cls.isAnnotationPresent(Provider.class)) {
                throw new IllegalArgumentException(
                        cls.getName() + " is not annotated as @" + Provider.class.getSimpleName());
            }
            mInfo = cls.getAnnotation(Provider.class);
        }

        return mInfo;
    }

    public Model[] getModels() {
        if (mModels == null) {
            Class<?>[] modelClasses = getProviderInfo().models();
            Model[] models = new Model[modelClasses.length];
            for (int i=0; i<models.length; ++i) {
                if (!modelClasses[i].isAnnotationPresent(Model.class)) {
                    throw new IllegalArgumentException(
                            modelClasses[i].getName() + " is not annotated as @" + Model.class.getSimpleName());
                }
                models[i] = modelClasses[i].getAnnotation(Model.class);
            }
            mModels = models;
        }
        return mModels;
    }

    ///// ContentProvider implementation

	@Override
	public boolean onCreate() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		int i = 0;
		String authority;
		String table;
		for (Model model : getModels()) {
			authority = model.authority();
			table = model.tableName();
			mUriMatcher.addURI(authority, table, i++);
			mUriMatcher.addURI(authority, table + "/#", i++);
		}

		mDBHelper = new DBHelper(getContext());
		return true;
    }

	@Override
	public String getType(Uri uri) {
		int match = mUriMatcher.match(uri);
		Model model = modelFromUriMatch(match);

		if (isItemUri(match)) return MIME_BASE_ITEM + model.contentType();
		return MIME_BASE_DIR + model.contentType();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = mUriMatcher.match(uri);
		Model model = modelFromUriMatch(match);

		if (isItemUri(match)) {
			selection = prependIdConstraint(uri, selection);
		}

		// Project columns
		HashMap<String,String> projectionMap = mProjectionMaps.get(model);
		String[] columns = null;
		if (projection != null) {
			columns = new String[projection.length];
			for (int i=0; i<projection.length; ++i) {
				columns[i] = projectionMap.get(projection[i]);
			}
		}

		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(
                model.tableName(),
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), getContentUri(model));
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
//		int match = mUriMatcher.match(uri);
//
//		if (isItemUri(match)) {
//			throw new IllegalArgumentException("Cannot insert to item URI: " + uri);
//		}
//
//		RecordInfo record = modelFromUriMatch(match);
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
//		RecordInfo record = modelFromUriMatch(mUriMatcher.match(uri));
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
//		RecordInfo record = modelFromUriMatch(mUriMatcher.match(uri));
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

	private Model modelFromUriMatch(int match) {
		Model[] models = getModels();
		int index = match / 2;
		if (match < 0 || index >= models.length) {
			throw new IllegalArgumentException("Invalid match: " + match);
		}
		return models[index];
	}

    private Uri getContentUri(Model model) {
        return new Uri.Builder()
                .scheme("content")
                .authority(model.authority())
                .appendPath(model.tableName())
                .build();
    }

}
