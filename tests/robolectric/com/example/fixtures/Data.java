package com.example.fixtures;


import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.annotations.Setter;
import net.xaethos.lib.activeprovider.models.ActiveModel;

import java.util.Date;

@ModelInfo(
        authority   = "com.example",
        tableName   = "data",
        contentType = "vnd.example.data"
)
public interface Data extends ActiveModel.Base, ActiveModel.Timestamps {
    public static final String STRING     = "string";
    public static final String BOOL       = "bool";
    public static final String BYTE       = "byte";
    public static final String SHORT      = "short";
    public static final String INT        = "int";
    public static final String LONG       = "long";
    public static final String FLOAT      = "float";
    public static final String DOUBLE     = "double";
    public static final String DATA       = "data";
    public static final String DATE       = "timestamp";

    @Getter(STRING) public String getString();
    @Setter(STRING) public void setString(String value);

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

}
