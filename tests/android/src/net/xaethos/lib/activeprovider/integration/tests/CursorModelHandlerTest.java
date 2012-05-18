package net.xaethos.lib.activeprovider.integration.tests;

import net.xaethos.lib.activeprovider.content.ActiveResolver;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.CursorModelHandler;

public class CursorModelHandlerTest extends ReadOnlyModelHandlerTest {

    @Override
    protected Polymorph newReadOnlyModelProxy(ActiveResolver.Cursor<Polymorph> cursor) {
        return new CursorModelHandler<Polymorph>(Polymorph.class, cursor).getModelProxy();
    }

}
