package net.xaethos.lib.activeprovider.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.CursorWrapper;
import net.xaethos.lib.activeprovider.models.ActiveModel;
import net.xaethos.lib.activeprovider.models.CursorModelHandler;

public class ActiveResolver extends ContentResolver {

    /////////////// Inner classes ///////////////

    public static class Cursor<T extends ActiveModel.Base> extends CursorWrapper {

        protected final Class<T> mModelClass;

        public Cursor(Class<T> cls, android.database.Cursor cursor) {
            super(cursor);
            mModelClass = cls;
        }

        public T getModel() {
            if (this.isClosed() || this.isBeforeFirst() || this.isAfterLast()) {
                return null;
            }
            return new CursorModelHandler<T>(mModelClass, this).getModelProxy();
        }

    }

    /////////////// Instance methods ///////////////

    public ActiveResolver(Context context) {
        super(context);
    }

    public <T extends ActiveModel.Base> Cursor<T> query(Class<T> modelClass, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return new Cursor<T>(modelClass, query(
                ActiveModel.getContentUri(modelClass), projection, selection, selectionArgs, sortOrder));
    }

    public <T extends ActiveModel.Base> Cursor<T> query(Class<T> modelClass) {
        return query(modelClass, null, null, null, null);
    }

    public <T extends ActiveModel.Base> Cursor<T> query(Class<T> modelClass, long id) {
        return new Cursor<T>(modelClass, query(
                ContentUris.withAppendedId(ActiveModel.getContentUri(modelClass), id), null, null, null, null));
    }

}
