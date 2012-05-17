package net.xaethos.lib.activeprovider.integration.tests;

import android.test.ProviderTestCase2;
import net.xaethos.lib.activeprovider.content.ActiveResolver;
import net.xaethos.lib.activeprovider.integration.MyProvider;
import net.xaethos.lib.activeprovider.integration.models.User;
import net.xaethos.lib.activeprovider.models.ActiveModel;

public class ActiveProviderTest extends ProviderTestCase2<MyProvider> {

    public ActiveProviderTest() {
        super(MyProvider.class, MyProvider.AUTHORITY);
    }

    ////////// Tests //////////

    public void testQuery() throws Exception {
        ActiveResolver.Cursor cursor = query(User.class);
        assertNotNull(cursor);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    ////////// Helper methods //////////

    protected <T extends ActiveModel.Base> ActiveResolver.Cursor<T> query(Class<T> cls) {
        return new ActiveResolver.Cursor<T>(cls, getMockContentResolver().query(
                ActiveModel.getContentUri(cls), null, null, null, null));
    }

}
