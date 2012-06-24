package net.xaethos.lib.activeprovider.models;

import android.content.*;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Proxy;
import java.util.*;

public class ModelManager {
    private static final String TAG = "ModelManager";

    /////////////// Static methods ///////////////

    protected static <T extends ActiveModel.Base> T getModel(Class<T> modelClass, ModelHandler<T> handler) {
        return modelClass.cast(Proxy.newProxyInstance(modelClass.getClassLoader(),
                new Class[]{modelClass},
                handler));
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
    public <T extends ActiveModel.Base> ModelCursor<T> query(Class<T> modelClass, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return query(modelClass, ActiveModel.getContentUri(modelClass), projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Queries the ContentProvider for the table representing a given model.
     * See {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.
     * @return a ModelCursor with the query results
     */
    public <T extends ActiveModel.Base> ModelCursor<T> query(Class<T> modelClass, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
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

    public <T extends ActiveModel.Base> T fetch(Class<T> modelClass, long id) {
        T model = null;
        ModelCursor<T> cursor = query(modelClass, ActiveModel.getContentUri(modelClass, id), null, null, null, null);
        if (cursor.moveToFirst()) {
            model = getModel(modelClass, new ValuesModelHandler<T>(modelClass, cursor));
            cursor.close();
        }
        return model;
    }

    public <T extends ActiveModel.Base> boolean save(T model) {
        ContentProviderOperation operation = model.saveOperation();
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(1);
        operations.add(operation);

        try {
            mResolver.applyBatch(operation.getUri().getAuthority(), operations);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Error saving model", e);
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error saving model", e);
        }

        return false;
    }

    /////////////// Inner classes ///////////////

    public final class ModelCursor<T extends ActiveModel.Base> extends CursorWrapper
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
            final T model = getModel(mModelType, new CursorModelHandler<T>(mModelType, this));

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

    private class ModelList<T extends ActiveModel.Base> extends AbstractList<T> {

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
