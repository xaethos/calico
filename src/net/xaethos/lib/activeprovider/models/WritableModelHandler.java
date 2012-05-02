package net.xaethos.lib.activeprovider.models;

@SuppressWarnings("UnusedDeclaration")
public interface WritableModelHandler {

    public abstract void set(String field, String value);
    public abstract void set(String field, Byte value);
    public abstract void set(String field, Short value);
    public abstract void set(String field, Integer value);
    public abstract void set(String field, Long value);
    public abstract void set(String field, Float value);
    public abstract void set(String field, Double value);
    public abstract void set(String field, Boolean value);
    public abstract void set(String field, byte[] value);

    public abstract void setNull(String field);

}
