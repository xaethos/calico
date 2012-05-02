package net.xaethos.lib.activeprovider.models;

import android.content.ContentValues;

public class ValuesModelHandler extends BaseModelHandler {

    private final ContentValues mContent;

    public ValuesModelHandler() {
        mContent = new ContentValues();
    }

    public ValuesModelHandler(ContentValues values) {
        mContent = new ContentValues(values);
    }

    @Override public String  getString(String field)    { return mContent.getAsString(field); }
    @Override public Boolean getBoolean(String field)   { return mContent.getAsBoolean(field); }
    @Override public Byte    getByte(String field)      { return mContent.getAsByte(field); }
    @Override public Short   getShort(String field)     { return mContent.getAsShort(field); }
    @Override public Integer getInteger(String field)   { return mContent.getAsInteger(field); }
    @Override public Long    getLong(String field)      { return mContent.getAsLong(field); }
    @Override public Float   getFloat(String field)     { return mContent.getAsFloat(field); }
    @Override public Double  getDouble(String field)    { return mContent.getAsDouble(field); }
    @Override public byte[]  getbyteArray(String field) { return mContent.getAsByteArray(field); }


    @Override public void set(String field, String value)  { mContent.put(field, value); }
    @Override public void set(String field, Boolean value) { mContent.put(field, value); }
    @Override public void set(String field, Byte value)    { mContent.put(field, value); }
    @Override public void set(String field, Short value)   { mContent.put(field, value); }
    @Override public void set(String field, Integer value) { mContent.put(field, value); }
    @Override public void set(String field, Long value)    { mContent.put(field, value); }
    @Override public void set(String field, Float value)   { mContent.put(field, value); }
    @Override public void set(String field, Double value)  { mContent.put(field, value); }
    @Override public void set(String field, byte[] value)  { mContent.put(field, value); }

    @Override public void setNull(String field) { mContent.putNull(field); }

}
