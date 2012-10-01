package net.xaethos.lib.calico.integration.tests;

import android.database.Cursor;
import net.xaethos.app.calicosample.models.Polymorph;
import net.xaethos.lib.calico.models.ValuesModelHandler;

public class ValuesModelHandlerTest extends ReadOnlyModelHandlerTest {

    @Override
    protected Polymorph newModelProxy(Cursor cursor) {
        return new ValuesModelHandler<Polymorph>(Polymorph.class, cursor).getModelProxy();
    }

}
