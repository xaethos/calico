package net.xaethos.lib.activeprovider.integration.models;

import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.integration.MyProvider;
import net.xaethos.lib.activeprovider.models.ActiveModel;

@ModelInfo(
        authority = MyProvider.AUTHORITY,
        contentType = "vnd.xaethos.test.user",
        tableName = "users")
public interface User extends ActiveModel.Base {

    public static final String NAME = "name";

}
