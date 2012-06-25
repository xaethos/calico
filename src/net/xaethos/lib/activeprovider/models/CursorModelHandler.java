package net.xaethos.lib.activeprovider.models;

import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

public class CursorModelHandler<T extends ActiveModel.Base> extends ModelHandler<T>
implements ReadOnlyModelHandler {

    private final Cursor mCursor;

    public CursorModelHandler(Class<T> modelInterface, Cursor cursor) {
        super(modelInterface);
        mCursor = cursor;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    ////////// ActiveModel.Base //////////

    @Override
    public Uri getUri() {
        Cursor cursor = mCursor;
        int index = cursor.getColumnIndex(ActiveModel.Base._ID);
        if (index < 0 || cursor.isNull(index)) {
            return null;
        }

        return ActiveModel.getContentUri(getModelInterface(), cursor.getLong(index));
    }

    @Override
    public T writableCopy() {
        Cursor cursor = mCursor;
        ValuesModelHandler<T> handler = new ValuesModelHandler<T>(getModelInterface(), cursor);
        return handler.getModelProxy();
    }

    ////////// ReadOnlyModelHandler //////////

    @Override public String  getString(String field)    { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getString(i); }
    @Override public Boolean getBoolean(String field)   { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getInt(i) != 0; }
    @Override public Byte    getByte(String field)      { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:(byte)c.getShort(i); }
    @Override public Short   getShort(String field)     { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getShort (i); }
    @Override public Integer getInteger(String field)   { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getInt(i); }
    @Override public Long    getLong(String field)      { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getLong(i); }
    @Override public Float   getFloat(String field)     { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getFloat(i); }
    @Override public Double  getDouble(String field)    { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getDouble(i); }
    @Override public byte[]  getbyteArray(String field) { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getBlob(i); }
    @Override public Date    getDate(String field)      { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:new Date(c.getLong(i)); }

}
