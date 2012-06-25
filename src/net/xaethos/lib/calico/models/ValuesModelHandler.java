package net.xaethos.lib.calico.models;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import java.util.Date;

public class ValuesModelHandler<T extends CalicoModel> extends ModelHandler<T>
implements ReadWriteModelHandler {

    private final ContentValues mValues;

    public ValuesModelHandler(Class<T> modelInterface) {
        super(modelInterface);
        mValues = new ContentValues();
    }

    public ValuesModelHandler(Class<T> modelInterface, ContentValues values) {
        super(modelInterface);
        mValues = new ContentValues(values);
    }

    public ValuesModelHandler(Class<T> modelInterface, Cursor cursor) {
        super(modelInterface);
        ContentValues values = null;
        if (cursor instanceof ModelManager.ModelCursor) {
            values = ((ModelManager.ModelCursor)cursor).getValues();
        }
        else {
            values = new ContentValues(cursor.getColumnCount());
            DatabaseUtils.cursorRowToContentValues(cursor, values);
        }
        mValues = values;
    }

    public ContentValues getValues() {
        return mValues;
    }

    ///// Helper methods

    private ContentProviderOperation insertOperation() {
        Uri uri = ModelManager.getContentUri(getModelInterface());
        return ContentProviderOperation
                .newInsert(uri)
                .withValues(mValues)
                .build();
    }

    private ContentProviderOperation updateOperation() {
        Uri uri = getUri();
        return ContentProviderOperation
                .newUpdate(uri)
                .withExpectedCount(1)
                .withValues(mValues)
                .build();
    }

    ////////// CalicoModel implementation //////////

    @Override
    public T writableCopy() {
        return new ValuesModelHandler<T>(getModelInterface()).getModelProxy();
    }

    @Override
    public ContentProviderOperation saveOperation() {
        Long id = mValues.getAsLong(CalicoModel._ID);
        if (id != null && id > 0) {
            return updateOperation();
        }
        else {
            return insertOperation();
        }
    }

    @Override
    public ContentProviderOperation deleteOperation() {
        Uri uri = getUri();
        return ContentProviderOperation
                .newDelete(uri)
                .withExpectedCount(1)
                .build();
    }

    ////////// ReadWriteModelHandler //////////

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
