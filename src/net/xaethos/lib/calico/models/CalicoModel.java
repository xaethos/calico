package net.xaethos.lib.calico.models;

import android.provider.BaseColumns;
import net.xaethos.lib.calico.annotations.Getter;

public interface CalicoModel<T extends CalicoModel> extends ModelManager.Utils<T> {
    public static final String _ID = BaseColumns._ID;

    @Getter(_ID) public Long getId();
}
