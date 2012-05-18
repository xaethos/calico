package net.xaethos.lib.activeprovider.integration;

import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.content.ActiveProvider;
import net.xaethos.lib.activeprovider.integration.migrations.AddTimestampsToPolymorphs;
import net.xaethos.lib.activeprovider.integration.migrations.CreatePolymorphs;
import net.xaethos.lib.activeprovider.integration.migrations.CreateUsers;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.integration.models.User;

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
public class MyProvider extends ActiveProvider {
    public static final String AUTHORITY = "net.xaethos.lib.activeprovider.integration";
}
