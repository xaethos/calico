package net.xaethos.lib.calico.models;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;

import java.util.*;

/**
* Created with IntelliJ IDEA.
* User: xaethos
* Date: 25/6/12
* Time: 14:20
* To change this template use File | Settings | File Templates.
*/
public final class ModelCursor<T extends CalicoModel> extends CursorWrapper
        implements Iterable<T> {
    private final Cursor mCursor;
    private final Class<T> mModelType;
    private final ContentProviderClient mContentProvider;
    private boolean mCloseFlag = false;

    ModelCursor(Cursor cursor, ContentProviderClient client, Class<T> modelType) {
        super(cursor);
        mCursor = cursor;
        mModelType = modelType;
        mContentProvider = client;
    }

    ////////// Instance methods //////////

    public List<T> getList() {
        return new ModelList();
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

    /////////////// Inner classes ///////////////

    private class ModelList extends AbstractList<T> {

        @Override
        public T get(int i) {
            Cursor cursor = ModelCursor.this;
            cursor.moveToPosition(i);
            return ModelManager.getModel(mModelType, new ValuesModelHandler<T>(mModelType, cursor));
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
