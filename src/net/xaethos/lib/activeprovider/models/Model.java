package net.xaethos.lib.activeprovider.models;

import android.provider.BaseColumns;
import net.xaethos.lib.activeprovider.annotations.Getter;

public interface Model<T extends Model> extends ModelManager.Utils<T> {
    public static final String _ID = BaseColumns._ID;

    @Getter(_ID) public Long getId();
}
