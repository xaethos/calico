package net.xaethos.lib.calico.integration.tests;

import android.database.Cursor;
import net.xaethos.lib.calico.integration.models.Polymorph;
import net.xaethos.lib.calico.models.CursorModelHandler;

public class CursorModelHandlerTest extends ReadOnlyModelHandlerTest {

    @Override
    protected Polymorph newModelProxy(Cursor cursor) {
        return new CursorModelHandler<Polymorph>(Polymorph.class, cursor).getModelProxy();
    }

}
