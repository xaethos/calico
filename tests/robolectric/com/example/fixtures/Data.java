package com.example.fixtures;


import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;

import java.util.Date;

@Model(
        authority   = "com.example",
        tableName   = "data",
        contentType = "vnd.example.data"
)
public interface Data {
//    public static final String ID         = BaseColumns._ID;
    public static final String FOO        = "foo";
    public static final String BAR        = "bar";
    public static final String BOOL       = "bool";
    public static final String BYTE       = "byte";
    public static final String SHORT      = "short";
    public static final String INT        = "int";
    public static final String LONG       = "long";
    public static final String FLOAT      = "float";
    public static final String DOUBLE     = "double";
    public static final String DATA       = "data";
    public static final String DATE       = "timestamp";
    public static final String CREATED_AT = "created_at";
//    public static final String UPDATED_AT = "updated_at";

//    @Getter(ID) public Long getId();

    @Getter(FOO) public String getFoo();
    @Setter(FOO) public void setFoo(String value);

//    @Getter(BAR) public String getBar();
//    @Setter(BAR) public void setBar(String value);

    @Getter(BOOL) public Boolean getBool();
    @Setter(BOOL) public void setBool(Boolean value);

    @Getter(BYTE) public Byte getByte();
    @Setter(BYTE) public void setByte(Byte value);

    @Getter(SHORT) public Short getShort();
    @Setter(SHORT) public void setShort(Short value);

    @Getter(INT) public Integer getInt();
    @Setter(INT) public void setInt(Integer value);

    @Getter(LONG) public Long getLong();
    @Setter(LONG) public void setLong(Long value);

    @Getter(FLOAT) public Float getFloat();
    @Setter(FLOAT) public void setFloat(Float value);

    @Getter(DOUBLE) public Double getDouble();
    @Setter(DOUBLE) public void setDouble(Double value);

    @Getter(DATA) public byte[] getData();
    @Setter(DATA) public void setData(byte[] value);

    @Getter(DATE) public Date getTimestamp();
    @Setter(DATE) public void setTimestamp(Date value);

//    @Getter(CREATED_AT) public Date getCreatedAt();
//    @Getter(UPDATED_AT) public Date getUpdatedAt();
}
