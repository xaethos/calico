package net.xaethos.lib.calico.content;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import net.xaethos.lib.calico.annotations.ModelInfo;
import net.xaethos.lib.calico.annotations.ProviderInfo;
import net.xaethos.lib.calico.models.CalicoModel;
import net.xaethos.lib.calico.models.ModelManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public abstract class CalicoProvider extends ContentProvider {

    public static final String MIGRATIONS_TABLE = "calico_migrations";

    /////////////// Inner classes ///////////////

	public static class DBHelper extends SQLiteOpenHelper {

        public static void createMigrationsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + MIGRATIONS_TABLE + "(name TEXT)");
        }

        public static String[] queryTableNames(SQLiteDatabase db) {
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            String[] tableNames = new String[cursor.getCount()];
            for (int i=0; i<tableNames.length; ++i) {
                cursor.moveToPosition(i);
                tableNames[i] = cursor.getString(0);
            }
            cursor.close();
            return tableNames;
        }

        private final ProviderInfo mProviderInfo;
        private final int mProviderVersion;

        private String[] mTableNames;

        public DBHelper(Context context, ProviderInfo info) {
            super(context, info.databaseName(), null, info.migrations().length + 1);
            mProviderInfo = info;
            mProviderVersion = info.migrations().length + 1;
		}

        public String[] getTableNames() {
            return mTableNames;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createMigrationsTable(db);
            if (mProviderVersion > 1) {
                onUpgrade(db, 1, mProviderVersion);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (ProviderMigration migration : getMissingMigrations(db)) {
                migration.upgrade(db);
            }
        }

        @SuppressWarnings("unused")
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new UnsupportedOperationException("CalicoProvider cannot downgrade database");
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            mTableNames = queryTableNames(db);
        }

        public List<ProviderMigration> getMissingMigrations(SQLiteDatabase db) {
            SQLiteStatement statement = db.compileStatement(
                    "SELECT COUNT() FROM " + MIGRATIONS_TABLE + " WHERE name=?");
            ArrayList<ProviderMigration> missingMigrations = new ArrayList<ProviderMigration>();

            try {
                for (Class<? extends ProviderMigration> migration : mProviderInfo.migrations()) {
                    statement.bindString(1, migration.getSimpleName());
                    if (statement.simpleQueryForLong() == 0) {
                        missingMigrations.add(migration.newInstance());
                    }
                }
            }
            catch (InstantiationException e) {
                throw new MigrationException("Couldn't instantiate migration", e);
            }
            catch (IllegalAccessException e) {
                throw new MigrationException("Couldn't instantiate migration", e);
            }
            finally {
                statement.close();
            }

            return missingMigrations;
        }

        public int getProviderVersion() {
            return mProviderVersion;
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

    private ProviderInfo mInfo;
    private ModelInfo[] mModels;

    protected DBHelper mDBHelper;
	protected UriMatcher mUriMatcher;
    protected HashMap<ModelInfo,HashMap<String,String>> mProjectionMaps;

	/////////////// Instance methods ///////////////

	///// Provider info

    public ProviderInfo getProviderInfo() {
        if (mInfo == null) {
            Class<? extends CalicoProvider> cls = this.getClass();
            if (!cls.isAnnotationPresent(ProviderInfo.class)) {
                throw new IllegalArgumentException(
                        cls.getName() + " is not annotated as @" + ProviderInfo.class.getSimpleName());
            }
            mInfo = cls.getAnnotation(ProviderInfo.class);
        }

        return mInfo;
    }

    public ModelInfo[] getModels() {
        if (mModels == null) {
            Class<? extends CalicoModel>[] modelInterfaces = getProviderInfo().models();
            ModelInfo[] models = new ModelInfo[modelInterfaces.length];
            for (int i=0; i<modelInterfaces.length; ++i) {
                models[i] = ModelManager.getModelInfo(modelInterfaces[i]);
            }
            mModels = models;
        }
        return mModels;
    }

    ///// ContentProvider implementation

	@Override
	public boolean onCreate() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mProjectionMaps = new HashMap<ModelInfo, HashMap<String, String>>();

		int i = 0;
		String authority;
		String table;
		for (ModelInfo model : getModels()) {
			authority = model.authority();
			table = model.tableName();
			mUriMatcher.addURI(authority, table, i++);
			mUriMatcher.addURI(authority, table + "/#", i++);
		}

		mDBHelper = new DBHelper(getContext(), getProviderInfo());
		return true;
    }

	@Override
	public String getType(Uri uri) {
		int match = mUriMatcher.match(uri);
		ModelInfo model = modelFromUriMatch(match);

		if (isItemUri(match)) {
            return ModelManager.getContentItemType(model);
        }
        else {
            return ModelManager.getContentDirType(model);
        }
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = mUriMatcher.match(uri);
		ModelInfo model = modelFromUriMatch(match);

		if (isItemUri(match)) {
			selection = prependIdConstraint(uri, selection);
		}

		// Project columns
		HashMap<String,String> projectionMap = getProjectionMap(model);
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
		cursor.setNotificationUri(getContext().getContentResolver(), ModelManager.getContentUri(model));
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = mUriMatcher.match(uri);

		if (isItemUri(match)) {
			throw new IllegalArgumentException("Cannot insert to item URI: " + uri);
		}

		ModelInfo model = modelFromUriMatch(match);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		long id = db.insert(model.tableName(), null, values);
		if (id < 0) return null;

		getContext().getContentResolver().notifyChange(uri, null);
		return ContentUris.withAppendedId(ModelManager.getContentUri(model), id);
	}

	@Override
	public int update(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		ModelInfo model = modelFromUriMatch(mUriMatcher.match(uri));
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		if (isItemUri(mUriMatcher.match(uri))) {
			selection = prependIdConstraint(uri, selection);
		}

		int count = db.update(model.tableName(), values, selection, selectionArgs);
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		ModelInfo model = modelFromUriMatch(mUriMatcher.match(uri));
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		if (isItemUri(mUriMatcher.match(uri))) {
			selection = prependIdConstraint(uri, selection);
		}

		int count = db.delete(model.tableName(), selection, selectionArgs);
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	///// Helper methods

	private ModelInfo modelFromUriMatch(int match) {
		ModelInfo[] models = getModels();
		int index = match / 2;
		if (match < 0 || index >= models.length) {
			throw new IllegalArgumentException("Invalid match: " + match);
		}
		return models[index];
	}

    private HashMap<String,String> getProjectionMap(ModelInfo model) {
        if (!mProjectionMaps.containsKey(model)) {
            String tableName = model.tableName();
            validateSQLNameOrThrow(tableName);
            String baseName = tableName + ".";
            Cursor cursor = mDBHelper.getReadableDatabase().rawQuery(
                    "PRAGMA table_info('" + tableName + "');", null);
            HashMap<String,String> projection =
                    new HashMap<String, String>(cursor.getCount());

            if (cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    String columnName = cursor.getString(1);
                    projection.put(columnName, baseName + columnName);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            mProjectionMaps.put(model, projection);
        }
        return mProjectionMaps.get(model);
    }

    private boolean validateSQLName(CharSequence name) {
        return Pattern.matches("\\w+", name);
    }

    private void validateSQLNameOrThrow(CharSequence name) {
        if (!validateSQLName(name)) {
            throw new IllegalArgumentException("the name '" + name + "' is invalid");
        }
    }

}
