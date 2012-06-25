package net.xaethos.lib.activeprovider.integration.tests;

import android.database.Cursor;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.ActiveModel;
import net.xaethos.lib.activeprovider.models.ModelManager;

public abstract class ReadOnlyModelHandlerTest extends BaseProviderTest {

    protected abstract Polymorph newModelProxy(Cursor cursor);

    ////////// Tests //////////

    public void test_getUri() {
        insertPolymorph("8");
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());

        Polymorph polymorph = newModelProxy(cursor);

        assertEquals(ActiveModel.getContentUri(Polymorph.class, polymorph.getId()), polymorph.getUri());
    }

    public void test_getUri_isNullIfNoId() {
        insertPolymorph("8");
        cursor = new ModelManager(getMockContext()).query(Polymorph.class, new String[]{Polymorph.VALUE}, null, null, null);
        assertTrue(cursor.moveToFirst());

        Polymorph polymorph = newModelProxy(cursor);

        assertNull(polymorph.getUri());
    }

    public void testTextPolymorph() throws Exception {
        insertPolymorph("8");
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        verifyTypeCasting(newModelProxy(cursor), "8");
    }

    public void testBooleanPolymorph() throws Exception {
        insertPolymorph(true);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        verifyTypeCasting(newModelProxy(cursor), "1");
    }

    public void testIntegerPolymorph() throws Exception {
        insertPolymorph(8);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        verifyTypeCasting(newModelProxy(cursor), "8");
    }

    public void testRealPolymorph() throws Exception {
        insertPolymorph(8.0);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        verifyTypeCasting(newModelProxy(cursor), "8");
    }

//    TODO: get blobs to work
    public void testBlobPolymorph() throws Exception {
        byte[] blob = { 1, 2, 3, 4 };
        insertPolymorph(blob);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());

        Polymorph polymorph = newModelProxy(cursor);
        for (int i=0; i<blob.length; ++i) {
            assertEquals(blob[i], polymorph.getBlobValue()[i]);
        }
    }

    ////////// Helper methods //////////

    protected void verifyTypeCasting(Polymorph poly, String value) throws Exception {
        assertEquals(value, poly.getStringValue());
        // TODO: assertTrue(poly.getBooleanValue());
        assertEquals(Byte.valueOf(value), poly.getByteValue());
        assertEquals(Short.valueOf(value), poly.getShortValue());
        assertEquals(Integer.valueOf(value), poly.getIntegerValue());
        assertEquals(Long.valueOf(value), poly.getLongValue());
        assertEquals(Float.valueOf(value), poly.getFloatValue());
        assertEquals(Double.valueOf(value), poly.getDoubleValue());
    }

}
