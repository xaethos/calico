package net.xaethos.lib.calico.integration;

import net.xaethos.lib.calico.annotations.ProviderInfo;
import net.xaethos.lib.calico.content.CalicoProvider;
import net.xaethos.lib.calico.integration.migrations.AddTimestampsToPolymorphs;
import net.xaethos.lib.calico.integration.migrations.CreatePolymorphs;
import net.xaethos.lib.calico.integration.migrations.CreateUsers;
import net.xaethos.lib.calico.integration.models.Polymorph;
import net.xaethos.lib.calico.integration.models.User;

@ProviderInfo(
        databaseName = "text.db",
        models = {
                User.class,
                Polymorph.class
        },
        migrations = {
                CreateUsers.class,
                CreatePolymorphs.class,
                AddTimestampsToPolymorphs.class
        }
)
public class MyProvider extends CalicoProvider {
    public static final String AUTHORITY = "net.xaethos.lib.calico.integration";
}
