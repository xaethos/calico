package net.xaethos.lib.activeprovider.integration.tests;

import android.content.ContentValues;
import android.net.Uri;
import android.test.ProviderTestCase2;
import net.xaethos.lib.activeprovider.content.ActiveResolver;
import net.xaethos.lib.activeprovider.integration.MyProvider;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.ActiveModel;

public abstract class ReadOnlyModelHandlerTest extends ProviderTestCase2<MyProvider> {

    ActiveResolver.Cursor<Polymorph> cursor;
    Polymorph polymorph;

    public ReadOnlyModelHandlerTest() {
        super(MyProvider.class, MyProvider.AUTHORITY);
    }

    protected abstract Polymorph newReadOnlyModelProxy(ActiveResolver.Cursor<Polymorph> cursor);

    @Override
    protected void tearDown() throws Exception {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        super.tearDown();
    }

    ////////// Tests //////////

    public void testTextPolymorph() throws Exception {
        insertPolymorph("8");
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        verifyTypeCasting(newReadOnlyModelProxy(cursor), "8");
    }

    public void testIntegerPolymorph() throws Exception {
        insertPolymorph(8);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        verifyTypeCasting(newReadOnlyModelProxy(cursor), "8");
    }

    public void testRealPolymorph() throws Exception {
        insertPolymorph(8.0);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        verifyTypeCasting(newReadOnlyModelProxy(cursor), "8");
    }

    public void testBlobPolymorph() throws Exception {
        byte[] blob = { 1, 2, 3, 4 };
        insertPolymorph(blob);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());

        polymorph = newReadOnlyModelProxy(cursor);
        for (int i=0; i<blob.length; ++i) {
            assertEquals(blob[i], polymorph.getBlobValue()[i]);
        }
    }

    ////////// Helper methods //////////

    protected ActiveResolver.Cursor<Polymorph> queryPolymorphs() {
        return new ActiveResolver.Cursor<Polymorph>(Polymorph.class, getMockContentResolver().query(
                ActiveModel.getContentUri(Polymorph.class), null, null, null, null));
    }

    protected Uri insertPolymorph(String value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return getMockContentResolver().insert(ActiveModel.getContentUri(Polymorph.class), values);
    }

    protected Uri insertPolymorph(Integer value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return getMockContentResolver().insert(ActiveModel.getContentUri(Polymorph.class), values);
    }

    protected Uri insertPolymorph(Double value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return getMockContentResolver().insert(ActiveModel.getContentUri(Polymorph.class), values);
    }

    protected Uri insertPolymorph(byte[] value) {
        ContentValues values = new ContentValues(1); values.put(Polymorph.VALUE, value);
        return getMockContentResolver().insert(ActiveModel.getContentUri(Polymorph.class), values);
    }

    protected void verifyTypeCasting(Polymorph poly, String value) throws Exception {
        assertEquals(value, poly.getStringValue());
        assertTrue(poly.getBooleanValue());
        assertEquals(Byte.valueOf(value), poly.getByteValue());
        assertEquals(Short.valueOf(value), poly.getShortValue());
        assertEquals(Integer.valueOf(value), poly.getIntegerValue());
        assertEquals(Long.valueOf(value), poly.getLongValue());
        assertEquals(Float.valueOf(value), poly.getFloatValue());
        assertEquals(Double.valueOf(value), poly.getDoubleValue());
    }

}
