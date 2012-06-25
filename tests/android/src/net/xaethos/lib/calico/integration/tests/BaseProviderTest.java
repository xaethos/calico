package net.xaethos.lib.calico.integration.tests;

import android.content.ContentValues;
import android.net.Uri;
import android.test.ProviderTestCase2;
import net.xaethos.lib.calico.annotations.ModelInfo;
import net.xaethos.lib.calico.annotations.ProviderInfo;
import net.xaethos.lib.calico.integration.MyProvider;
import net.xaethos.lib.calico.integration.models.Polymorph;
import net.xaethos.lib.calico.models.ModelManager;

public class BaseProviderTest extends ProviderTestCase2<MyProvider> {

    static final String[] ID_PROJECTION = { Polymorph._ID };
    static final String[] VALUE_PROJECTION = { Polymorph.VALUE };

    ProviderInfo providerInfo;

    ModelManager.ModelCursor<Polymorph> cursor;
    ModelInfo polymorphInfo;
    Uri polymorphUri;

    public BaseProviderTest() {
        super(MyProvider.class, MyProvider.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        providerInfo = MyProvider.class.getAnnotation(ProviderInfo.class);
        polymorphInfo = Polymorph.class.getAnnotation(ModelInfo.class);
        polymorphUri = Uri.parse("content://" + polymorphInfo.authority() + "/" + polymorphInfo.tableName());
    }

    @Override
    protected void tearDown() throws Exception {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        super.tearDown();
    }

    public void testMockResolver() throws Exception {
        assertEquals(getMockContext().getContentResolver(), getMockContentResolver());
    }

    ////////// Helper methods //////////

    protected ModelManager.ModelCursor<Polymorph> queryPolymorphs() {
        return new ModelManager(getMockContext()).query(Polymorph.class, null, null, null, null);
    }

    protected Uri insertPolymorph(String value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return insertPolymorph(values);
    }

    protected Uri insertPolymorph(Boolean value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return insertPolymorph(values);
    }

    protected Uri insertPolymorph(Integer value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return insertPolymorph(values);
    }

    protected Uri insertPolymorph(Double value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return insertPolymorph(values);
    }

    protected Uri insertPolymorph(byte[] value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return insertPolymorph(values);
    }

    protected Uri insertPolymorph(ContentValues values) {
        values.put(Polymorph._CREATED_AT, System.currentTimeMillis());
        values.put(Polymorph._UPDATED_AT, values.getAsLong(Polymorph._CREATED_AT));
        return getMockContentResolver().insert(polymorphUri, values);
    }

}
