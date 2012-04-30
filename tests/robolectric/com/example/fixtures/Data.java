package com.example.fixtures;


import android.provider.BaseColumns;
import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;

@Model(
        authority   = "com.example",
        tableName   = "data",
        contentType = "vnd.example.data"
)
public interface Data {
    public static final String ID         = BaseColumns._ID;
    public static final String FOO        = "foo";
    public static final String BAR        = "bar";
    public static final String CREATED_AT = "created_at";

    @Getter(ID) public Long getId();

    @Getter(FOO) public String getFoo();
    @Setter(FOO) public String setFoo();

    @Getter(BAR) public String getBar();
    @Setter(BAR) public String setBar();

    @Getter(CREATED_AT) public Long getCreatedAt();
}
