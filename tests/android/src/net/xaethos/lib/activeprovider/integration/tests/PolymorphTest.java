package net.xaethos.lib.activeprovider.integration.tests;

import android.content.ContentValues;
import android.net.Uri;
import android.test.ProviderTestCase2;
import net.xaethos.lib.activeprovider.content.ActiveResolver;
import net.xaethos.lib.activeprovider.integration.MyProvider;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.ActiveModel;

public class PolymorphTest extends ProviderTestCase2<MyProvider> {

    ActiveResolver.Cursor<Polymorph> cursor;
    Polymorph polymorph;

    public PolymorphTest() {
        super(MyProvider.class, MyProvider.AUTHORITY);
    }

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

        polymorph = cursor.getModel();
        assertEquals("8", polymorph.getStringValue());
        assertTrue(polymorph.getBooleanValue());
        assertEquals(Byte.valueOf((byte) 8), polymorph.getByteValue());
        assertEquals(Short.valueOf((short) 8), polymorph.getShortValue());
        assertEquals(Integer.valueOf(8), polymorph.getIntegerValue());
        assertEquals(Long.valueOf(8L), polymorph.getLongValue());
        assertEquals(8.0f, polymorph.getFloatValue());
        assertEquals(8.0, polymorph.getDoubleValue());
    }

    public void testIntegerPolymorph() throws Exception {
        insertPolymorph(8);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());

        polymorph = cursor.getModel();
        assertEquals("8", polymorph.getStringValue());
        assertTrue(polymorph.getBooleanValue());
        assertEquals(Byte.valueOf((byte) 8), polymorph.getByteValue());
        assertEquals(Short.valueOf((short) 8), polymorph.getShortValue());
        assertEquals(Integer.valueOf(8), polymorph.getIntegerValue());
        assertEquals(Long.valueOf(8L), polymorph.getLongValue());
        assertEquals(8.0f, polymorph.getFloatValue());
        assertEquals(8.0, polymorph.getDoubleValue());
    }

    public void testRealPolymorph() throws Exception {
        insertPolymorph(8.0);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());

        polymorph = cursor.getModel();
        assertEquals("8", polymorph.getStringValue());
        assertTrue(polymorph.getBooleanValue());
        assertEquals(Byte.valueOf((byte) 8), polymorph.getByteValue());
        assertEquals(Short.valueOf((short) 8), polymorph.getShortValue());
        assertEquals(Integer.valueOf(8), polymorph.getIntegerValue());
        assertEquals(Long.valueOf(8L), polymorph.getLongValue());
        assertEquals(8.0f, polymorph.getFloatValue());
        assertEquals(8.0, polymorph.getDoubleValue());
    }

    public void testBlobPolymorph() throws Exception {
        byte[] blob = { 1, 2, 3, 4 };
        insertPolymorph(blob);
        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());

        polymorph = cursor.getModel();
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

}
