package net.xaethos.lib.activeprovider.models;

import android.content.ContentValues;

import java.util.Date;

public class ValuesModelHandler extends ModelHandler
implements ReadableModelHandler, WritableModelHandler {

    private final ContentValues mValues;

    public ValuesModelHandler() {
        mValues = new ContentValues();
    }

    public ValuesModelHandler(ContentValues values) {
        mValues = new ContentValues(values);
    }

    public ContentValues getValues() {
        return mValues;
    }

    @Override public String  getString(String field)    { return mValues.getAsString(field); }
    @Override public Boolean getBoolean(String field)   { return mValues.getAsBoolean(field); }
    @Override public Byte    getByte(String field)      { return mValues.getAsByte(field); }
    @Override public Short   getShort(String field)     { return mValues.getAsShort(field); }
    @Override public Integer getInteger(String field)   { return mValues.getAsInteger(field); }
    @Override public Long    getLong(String field)      { return mValues.getAsLong(field); }
    @Override public Float   getFloat(String field)     { return mValues.getAsFloat(field); }
    @Override public Double  getDouble(String field)    { return mValues.getAsDouble(field); }
    @Override public byte[]  getbyteArray(String field) { return mValues.getAsByteArray(field); }

    @Override public void set(String field, String value)  { mValues.put(field, value); }
    @Override public void set(String field, Boolean value) { mValues.put(field, value); }
    @Override public void set(String field, Byte value)    { mValues.put(field, value); }
    @Override public void set(String field, Short value)   { mValues.put(field, value); }
    @Override public void set(String field, Integer value) { mValues.put(field, value); }
    @Override public void set(String field, Long value)    { mValues.put(field, value); }
    @Override public void set(String field, Float value)   { mValues.put(field, value); }
    @Override public void set(String field, Double value)  { mValues.put(field, value); }
    @Override public void set(String field, byte[] value)  { mValues.put(field, value); }

    @Override public void setNull(String field) { mValues.putNull(field); }

    @Override public Date getDate(String field) {
        Long milliseconds = mValues.getAsLong(field);
        if (milliseconds != null) {
            return new Date(milliseconds);
        }
        else {
            return null;
        }
    }
    @Override public void set(String field, Date value) {
        mValues.put(field, value.getTime());
    }

}
