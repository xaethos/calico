package net.xaethos.lib.activeprovider.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.CursorWrapper;
import net.xaethos.lib.activeprovider.models.ActiveModel;
import net.xaethos.lib.activeprovider.models.CursorModelHandler;
import net.xaethos.lib.activeprovider.models.ModelManager;

public class ActiveResolver extends ContentResolver {

    /////////////// Inner classes ///////////////

    public static class Cursor<T extends ActiveModel> extends CursorWrapper {

        protected final Class<T> mModelClass;

        public Cursor(Class<T> cls, android.database.Cursor cursor) {
            super(cursor);
            mModelClass = cls;
        }

        T getModel() {
            if (this.isClosed() || this.isBeforeFirst() || this.isAfterLast()) {
                return null;
            }
            return CursorModelHandler.newModelInstance(
                    mModelClass, new CursorModelHandler(this));
        }

    }

    /////////////// Instance methods ///////////////

    public ActiveResolver(Context context) {
        super(context);
    }

    public <T extends ActiveModel> Cursor<T> query(Class<T> modelClass, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return new Cursor<T>(modelClass, query(
                ModelManager.getContentUri(modelClass), projection, selection, selectionArgs, sortOrder));
    }

    public <T extends ActiveModel> Cursor<T> query(Class<T> modelClass) {
        return query(modelClass, null, null, null, null);
    }

    public <T extends ActiveModel> Cursor<T> query(Class<T> modelClass, long id) {
        return new Cursor<T>(modelClass, query(
                ContentUris.withAppendedId(ModelManager.getContentUri(modelClass), id), null, null, null, null));
    }

}
