package net.xaethos.app.calicosample;

import net.xaethos.app.calicosample.migrations.AddTimestampsToPolymorphs;
import net.xaethos.app.calicosample.migrations.CreatePolymorphs;
import net.xaethos.app.calicosample.migrations.CreateUsers;
import net.xaethos.app.calicosample.models.Polymorph;
import net.xaethos.app.calicosample.models.User;
import net.xaethos.lib.calico.annotations.ProviderInfo;
import net.xaethos.lib.calico.content.CalicoProvider;

@ProviderInfo(
        databaseName = "test.db",
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
    public static final String AUTHORITY = "net.xaethos.app.calicosample";
}
