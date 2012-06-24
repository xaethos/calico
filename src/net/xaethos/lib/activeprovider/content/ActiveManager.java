package net.xaethos.lib.activeprovider.content;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.RemoteException;
import net.xaethos.lib.activeprovider.models.ActiveModel;
import net.xaethos.lib.activeprovider.models.CursorModelHandler;
import net.xaethos.lib.activeprovider.models.ValuesModelHandler;

import java.lang.reflect.Proxy;
import java.util.*;

public class ActiveManager {

    /////////////// Instance fields ///////////////

    private final ContentResolver mResolver;

    /////////////// Instance methods ///////////////

    public ActiveManager(Context context) {
        mResolver = context.getContentResolver();
    }

    /**
     * Queries the ContentProvider for the table representing a given model.
     * Equivalent to calling query(modelClass, null, null, null, null)
     * @return a ModelCursor with the query results
     */
    public <T extends ActiveModel.Base> ModelCursor<T> query(Class<T> modelClass) {
        return query(modelClass, null, null, null, null);
    }

    /**
     * Queries the ContentProvider for the table representing a given model.
     * See {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.
     * @return a ModelCursor with the query results
     */
    public <T extends ActiveModel.Base> ModelCursor<T> query(Class<T> modelClass, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Uri uri = ActiveModel.getContentUri(modelClass);
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
            CursorModelHandler<T> handler = new CursorModelHandler<T>(mModelType, this);
            final T model = mModelType.cast(Proxy.newProxyInstance(mModelType.getClassLoader(),
                    new Class[]{mModelType},
                    handler));

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
            ValuesModelHandler<T> handler = new ValuesModelHandler<T>(mModelClass, mCursor);
            return mModelClass.cast(Proxy.newProxyInstance(mModelClass.getClassLoader(),
                    new Class[]{mModelClass},
                    handler));
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
