package net.xaethos.lib.activeprovider.models;

import android.database.Cursor;

public class CursorModelHandler extends BaseModelHandler {

    private final Cursor mCursor;

    public CursorModelHandler(Cursor cursor) {
        mCursor = cursor;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public Object get(String field, Class<?> type) {
        Cursor cursor = mCursor;
        int index = cursor.getColumnIndexOrThrow(field);

        if (type.isAssignableFrom(Long.class))    return cursor.getLong(index);
        if (type.isAssignableFrom(String.class))  return cursor.getString(index);
        if (type.isAssignableFrom(Integer.class)) return cursor.getInt(index);
        if (type.isAssignableFrom(Boolean.class)) return cursor.getInt(index) != 0;
        if (type.isAssignableFrom(byte[].class))  return cursor.getBlob(index);
        if (type.isAssignableFrom(Float.class))   return cursor.getFloat(index);
        if (type.isAssignableFrom(Double.class))  return cursor.getDouble(index);
        if (type.isAssignableFrom(Short.class))   return cursor.getShort(index);

        throw new UnsupportedOperationException("Cannot handle type " + type.getSimpleName());
    }

    @Override
    public void set(String field, Object value) {
        throw new UnsupportedOperationException();
    }

}
