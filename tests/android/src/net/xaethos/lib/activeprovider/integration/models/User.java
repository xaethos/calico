package net.xaethos.lib.activeprovider.integration.models;

import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.models.ActiveModel;

@ModelInfo(
        authority = "net.xaethos.lib.activeprovider.integration",
        contentType = "vnd.xaethos.test.user",
        tableName = "users")
public interface User extends ActiveModel {

    public static final String NAME = "name";

}
