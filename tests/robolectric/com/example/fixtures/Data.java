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
    public static final String INFO       = "info";
    public static final String CREATED_AT = "created_at";

    @Getter(ID) public Long getId();

    @Getter(INFO) public String getInfo();
    @Setter(INFO) public String setInfo();

    @Getter(CREATED_AT) public Long getCreatedAt();
}
