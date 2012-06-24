package net.xaethos.lib.activeprovider.integration.tests;

import android.database.Cursor;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.ValuesModelHandler;

public class ValuesModelHandlerTest extends ReadOnlyModelHandlerTest {

    @Override
    protected Polymorph newReadOnlyModelProxy(Cursor cursor) {
        return new ValuesModelHandler<Polymorph>(Polymorph.class, cursor).getModelProxy();
    }

}
