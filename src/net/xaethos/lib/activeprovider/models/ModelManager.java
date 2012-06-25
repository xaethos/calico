package net.xaethos.lib.activeprovider.models;

import android.content.*;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;

import java.lang.reflect.Proxy;
import java.util.*;

public class ModelManager {
    private static final String TAG = "ModelManager";

    /////////////// Static methods ///////////////

    public static <T extends Model> ModelInfo getModelInfo(Class<T> modelType) {
        if (!isModelInterface(modelType)) {
            throw new IllegalArgumentException(
                    modelType.getName() + "must be an interface with the annotation @" + ModelInfo.class.getSimpleName());
        }
        return modelType.getAnnotation(ModelInfo.class);
    }

    public static Uri getContentUri(ModelInfo model) {
        return buildContentUri(model).build();
    }

    public static <T extends Model> Uri getContentUri(Class<T> modelType) {
        return getContentUri(modelType.getAnnotation(ModelInfo.class));
    }

    public static Uri getContentUri(ModelInfo model, long id) {
        return buildContentUri(model).appendPath(Long.toString(id)).build();
    }

    public static <T extends Model> Uri getContentUri(Class<T> modelType, long id) {
        return getContentUri(modelType.getAnnotation(ModelInfo.class), id);
    }

    public static String getContentDirType(ModelInfo model) {
        return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + model.contentType();
    }

    public static <T extends Model> String getContentDirType(Class<T> modelType) {
        return getContentDirType(getModelInfo(modelType));
    }

    public static String getContentItemType(ModelInfo model) {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + model.contentType();
    }

    public static <T extends Model> String getContentItemType(Class<T> modelType) {
        return getContentItemType(getModelInfo(modelType));
    }

    ///// Helpers

    static <T extends Model> boolean isModelInterface(Class<T> modelType) {
        return modelType.isInterface() && modelType.isAnnotationPresent(ModelInfo.class);
    }

    static <T extends Model> T getModel(Class<T> modelClass, ModelHandler<T> handler) {
        return modelClass.cast(Proxy.newProxyInstance(modelClass.getClassLoader(),
                new Class[]{modelClass},
                handler));
    }

    private static Uri.Builder buildContentUri(ModelInfo model) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(model.authority())
                .appendPath(model.tableName());
    }

    /////////////// Instance fields ///////////////

    private final ContentResolver mResolver;

    /////////////// Instance methods ///////////////

    public ModelManager(Context context) {
        mResolver = context.getContentResolver();
    }

    /**
     * Queries the ContentProvider for the table representing a given model.
     * See {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.
     * @return a ModelCursor with the query results
     */
    public <T extends Model> ModelCursor<T> query(Class<T> modelClass, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return query(modelClass, getContentUri(modelClass), projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Queries the ContentProvider for the table representing a given model.
     * See {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.
     * @return a ModelCursor with the query results
     */
    public <T extends Model> ModelCursor<T> query(Class<T> modelClass, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ContentProviderClient client = mResolver.acquireContentProviderClient(uri);
        try {
            Cursor cursor = client.query(uri, projection, selection, selectionArgs, sortOrder);
            if (cursor == null) {
                client.release();
                return null;
            }
            return new ModelCursor<T>(cursor, client, modelClass);
        } catch (RemoteException e) {
            client.release();
            return null;
        } catch (RuntimeException e) {
            client.release();
            throw e;
        }
    }

    public <T extends Model> T create(Class<T> modelType) {
        return getModel(modelType, new ValuesModelHandler<T>(modelType));
    }

    public <T extends Model> T fetch(Class<T> modelClass, long id) {
        T model = null;
        ModelCursor<T> cursor = query(modelClass, getContentUri(modelClass, id), null, null, null, null);
        if (cursor.moveToFirst()) {
            model = getModel(modelClass, new ValuesModelHandler<T>(modelClass, cursor));
            cursor.close();
        }
        return model;
    }

    public <T extends Model> boolean save(T model) {
        return applyOperation(model.saveOperation());
    }

    public <T extends Model> boolean delete(T model) {
        return applyOperation(model.deleteOperation());
    }

    private boolean applyOperation(ContentProviderOperation operation) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(1);
        operations.add(operation);

        try {
            mResolver.applyBatch(operation.getUri().getAuthority(), operations);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Error invoking binder", e);
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error applying operation", e);
        }

        return false;
    }

    /////////////// Inner classes ///////////////

    @SuppressWarnings("UnusedDeclaration")
    public static interface Timestamps {
        public static final String _CREATED_AT = "_created_at";
        public static final String _UPDATED_AT = "_updated_at";

        @Getter(_CREATED_AT) public Date getCreatedAt();
        @Getter(_UPDATED_AT) public Date getUpdatedAt();
    }

    public static interface Utils<T extends Model> {
        public Uri getUri();

        public boolean isReadOnly();
        public T writableCopy();

        public ContentProviderOperation saveOperation();
        public ContentProviderOperation deleteOperation();
    }

    public final class ModelCursor<T extends Model> extends CursorWrapper
            implements Iterable<T> {
        private final Cursor mCursor;
        private final Class<T> mModelType;
        private final ContentProviderClient mContentProvider;
        private boolean mCloseFlag = false;

        private ModelCursor(Cursor cursor, ContentProviderClient client, Class<T> modelType) {
            super(cursor);
            mCursor = cursor;
            mModelType = modelType;
            mContentProvider = client;
        }

        ////////// Instance methods //////////

        public List<T> getList() {
            return new ModelList<T>(mModelType, this);
        }

        public T getModel() {
            return ModelManager.getModel(mModelType, new ValuesModelHandler<T>(mModelType, this));
        }

        public ContentValues getValues() {
            ContentValues values = new ContentValues(getColumnCount());
            DatabaseUtils.cursorRowToContentValues(mCursor, values);
            return values;
        }

        @Override
        public void close() {
            super.close();
            mContentProvider.release();
            mCloseFlag = true;
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                if(!mCloseFlag) {
                    mContentProvider.release();
                }
            } finally {
                super.finalize();
            }
        }

        ////////// Iterable<T> //////////

        @Override
        public Iterator<T> iterator() {
            moveToPosition(-1);
            final T model = ModelManager.getModel(mModelType, new CursorModelHandler<T>(mModelType, this));

            return new Iterator<T>() {
                private final T mModel = model;

                @Override
                public boolean hasNext() {
                    return !isLast();
                }

                @Override
                public T next() {
                    moveToNext();
                    if (isAfterLast()) throw new NoSuchElementException();
                    return mModel;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Immutable collection");
                }
            };
        }
    }

    private class ModelList<T extends Model> extends AbstractList<T> {

        private final Class<T> mModelClass;
        private final ModelCursor mCursor;

        private ModelList(Class<T> modelClass, ModelCursor cursor) {
            mModelClass = modelClass;
            mCursor = cursor;
        }

        @Override
        public T get(int i) {
            mCursor.moveToPosition(i);
            return getModel(mModelClass, new ValuesModelHandler<T>(mModelClass, mCursor));
        }

        @Override
        public int size() {
            return mCursor.getCount();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Immutable collection");
        }

        @Override
        public boolean removeAll(Collection<?> objects) {
            throw new UnsupportedOperationException("Immutable collection");
        }

    }

}
