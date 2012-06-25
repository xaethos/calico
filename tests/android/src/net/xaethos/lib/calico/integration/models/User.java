package net.xaethos.lib.calico.integration.models;

import net.xaethos.lib.calico.annotations.Getter;
import net.xaethos.lib.calico.annotations.ModelInfo;
import net.xaethos.lib.calico.annotations.Setter;
import net.xaethos.lib.calico.integration.MyProvider;
import net.xaethos.lib.calico.models.CalicoModel;

@ModelInfo(
        authority = MyProvider.AUTHORITY,
        contentType = "vnd.xaethos.test.user",
        tableName = "users"
)
public interface User extends CalicoModel<User> {

    public static final String NAME = "name";

    @Getter(NAME) String getName();
    @Setter(NAME) void setName(String name);
}
