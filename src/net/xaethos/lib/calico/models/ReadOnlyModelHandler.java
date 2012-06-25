package net.xaethos.lib.calico.models;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public interface ReadOnlyModelHandler {

    public abstract String  getString(String field);
    public abstract Boolean getBoolean(String field);
    public abstract Byte    getByte(String field);
    public abstract Short   getShort(String field);
    public abstract Integer getInteger(String field);
    public abstract Long    getLong(String field);
    public abstract Float   getFloat(String field);
    public abstract Double  getDouble(String field);
    public abstract byte[]  getbyteArray(String field);
    public abstract Date    getDate(String field);

}
