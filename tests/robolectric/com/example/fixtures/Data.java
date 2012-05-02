package com.example.fixtures;


import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;

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
    public static final String SHORT      = "short";
    public static final String INT        = "int";
    public static final String LONG       = "long";
    public static final String FLOAT      = "float";
    public static final String DOUBLE     = "double";
    public static final String DATA       = "data";
    public static final String CREATED_AT = "created_at";
//    public static final String UPDATED_AT = "updated_at";

//    @Getter(ID) public Long getId();

    @Getter(FOO) public String getFoo();
    @Setter(FOO) public void setFoo(String value);

//    @Getter(BAR) public String getBar();
//    @Setter(BAR) public void setBar(String value);

    @Getter(BOOL) public Boolean getBool();
    @Setter(BOOL) public void setBool(Boolean value);

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

//    @Getter(CREATED_AT) public Long getCreatedAt();
//    @Getter(UPDATED_AT) public Long getUpdatedAt();
}
