package net.xaethos.lib.activeprovider.integration.tests;

import android.content.ContentUris;
import android.net.Uri;
import net.xaethos.lib.activeprovider.content.ActiveManager;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;

public class ActiveManagerTest extends BaseProviderTest {

    ActiveManager manager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        manager = new ActiveManager(getMockContext());
    }

    ////////// Tests //////////

    public void test_query_forModelCursor() {
        insertPolymorph("dining table");
        insertPolymorph("desk");
        insertPolymorph("coffee table");

        String[] projection = {Polymorph._ID, Polymorph.VALUE};
        String   where      = Polymorph.VALUE + " LIKE ?";
        String[] whereArgs  = {"%table"};
        String   orderBy    = Polymorph.VALUE + " ASC";

        cursor = manager.query(Polymorph.class, projection, where, whereArgs, orderBy);
        assertNotNull(cursor);
        assertEquals(2, cursor.getCount());
        assertEquals(Polymorph._ID, cursor.getColumnName(0));
        assertEquals(Polymorph.VALUE, cursor.getColumnName(1));
        assertTrue(cursor.moveToFirst());
        assertEquals("coffee table", cursor.getString(1));
        assertTrue(cursor.moveToNext());
        assertEquals("dining table", cursor.getString(1));
    }

    public void test_query_forModel() {
        Uri uri = insertPolymorph("dining table");

        Polymorph poly = manager.fetch(Polymorph.class, ContentUris.parseId(uri));

        assertNotNull(poly);
        assertEquals("dining table", poly.getStringValue());
        assertFalse(poly.isReadOnly());
    }

}
