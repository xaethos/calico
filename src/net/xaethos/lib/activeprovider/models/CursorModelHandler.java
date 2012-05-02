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

    @Override public String  getString(String field)    { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getString(i); }
    @Override public Boolean getBoolean(String field)   { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getInt(i) != 0; }
    @Override public Byte    getByte(String field)      { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:(byte)c.getShort(i); }
    @Override public Short   getShort(String field)     { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getShort (i); }
    @Override public Integer getInteger(String field)   { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getInt   (i); }
    @Override public Long    getLong(String field)      { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getLong  (i); }
    @Override public Float   getFloat(String field)     { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getFloat (i); }
    @Override public Double  getDouble(String field)    { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getDouble(i); }
    @Override public byte[]  getbyteArray(String field) { Cursor c = mCursor; int i = c.getColumnIndexOrThrow(field); return c.isNull(i)?null:c.getBlob  (i); }

    @Override public void set(String field, String value)  { throw new UnsupportedOperationException(); }
    @Override public void set(String field, Boolean value) { throw new UnsupportedOperationException(); }
    @Override public void set(String field, Byte value)    { throw new UnsupportedOperationException(); }
    @Override public void set(String field, Short value)   { throw new UnsupportedOperationException(); }
    @Override public void set(String field, Integer value) { throw new UnsupportedOperationException(); }
    @Override public void set(String field, Long value)    { throw new UnsupportedOperationException(); }
    @Override public void set(String field, Float value)   { throw new UnsupportedOperationException(); }
    @Override public void set(String field, Double value)  { throw new UnsupportedOperationException(); }
    @Override public void set(String field, byte[] value)  { throw new UnsupportedOperationException(); }

    @Override public void setNull(String field) {}

}
