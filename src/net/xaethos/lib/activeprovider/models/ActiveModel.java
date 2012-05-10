package net.xaethos.lib.activeprovider.models;

import android.provider.BaseColumns;
import net.xaethos.lib.activeprovider.annotations.Getter;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public interface ActiveModel {
    public static final String _ID = BaseColumns._ID;
    public static final String _CREATED_AT = "created_at";
    public static final String _UPDATED_AT = "updated_at";

    @Getter(_ID) public Long getId();
    @Getter(_CREATED_AT) public Date getCreatedAt();
    @Getter(_UPDATED_AT) public Date getUpdatedAt();
}
