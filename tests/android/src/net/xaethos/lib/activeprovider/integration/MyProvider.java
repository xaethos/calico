package net.xaethos.lib.activeprovider.integration;

import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.content.ActiveProvider;
import net.xaethos.lib.activeprovider.integration.migrations.CreateUsers;
import net.xaethos.lib.activeprovider.integration.models.User;

@ProviderInfo(
        databaseName = "text.db",
        models = {User.class},
        migrations = {
                CreateUsers.class
        }
)
public class MyProvider extends ActiveProvider {}
