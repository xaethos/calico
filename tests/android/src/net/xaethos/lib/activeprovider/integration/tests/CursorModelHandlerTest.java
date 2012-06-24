package net.xaethos.lib.activeprovider.integration.tests;

import android.database.Cursor;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.CursorModelHandler;

public class CursorModelHandlerTest extends ReadOnlyModelHandlerTest {

    @Override
    protected Polymorph newReadOnlyModelProxy(Cursor cursor) {
        return new CursorModelHandler<Polymorph>(Polymorph.class, cursor).getModelProxy();
    }

}
