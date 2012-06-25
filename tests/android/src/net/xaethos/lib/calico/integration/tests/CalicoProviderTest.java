package net.xaethos.lib.calico.integration.tests;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import net.xaethos.lib.calico.annotations.ModelInfo;
import net.xaethos.lib.calico.integration.models.Polymorph;
import net.xaethos.lib.calico.models.ModelManager;

import static net.xaethos.lib.calico.integration.tests.Assert.assertHasItems;

public class CalicoProviderTest extends BaseProviderTest {

    ContentResolver resolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        resolver = getMockContentResolver();
    }

    ////////// Tests //////////

    public void test_getProviderInfo() {
        assertEquals(providerInfo, getProvider().getProviderInfo());
    }

    public void test_getModels() {
        ModelInfo[] models = getProvider().getModels();
        assertEquals(2, models.length);
        assertEquals(providerInfo.models()[0].getAnnotation(ModelInfo.class), models[0]);
        assertEquals(providerInfo.models()[1].getAnnotation(ModelInfo.class), models[1]);
    }

    public void test_getType() {
        String type = polymorphInfo.contentType();

        assertEquals("vnd.android.cursor.dir/" + type,
                resolver.getType(polymorphUri));

        assertEquals("vnd.android.cursor.item/" + type,
                resolver.getType(Uri.withAppendedPath(polymorphUri, "1")));
    }

    public void test_query() throws Exception {
        cursor = queryPolymorphs();
        assertNotNull(cursor);
        assertEquals(0, cursor.getCount());
    }

    public void test_query_handlesProjections() throws Exception {
        Cursor cursor;
        String[] projection = {
                Polymorph._ID,
                Polymorph.VALUE,
        };

        cursor = resolver.query(polymorphUri, projection, null, null, null);
        assertEquals(projection.length, cursor.getColumnCount());
        assertHasItems(projection, cursor.getColumnNames());
        cursor.close();

        cursor = resolver.query(polymorphUri, ID_PROJECTION, null, null, null);
        assertEquals(1, cursor.getColumnCount());
        assertEquals(BaseColumns._ID, cursor.getColumnNames()[0]);
        cursor.close();
    }

    public void test_query_handlesDirAndItemUris() {
        Cursor cursor;
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Hello");

        Uri itemUri = resolver.insert(polymorphUri, values);
        resolver.insert(polymorphUri, values);

        cursor = resolver.query(polymorphUri, ID_PROJECTION, null, null, null);
        assertEquals(2, cursor.getCount());
        cursor.close();

        cursor = resolver.query(itemUri, ID_PROJECTION, null, null, null);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(ContentUris.parseId(itemUri), cursor.getLong(0));
        cursor.close();
    }

    public void test_query_canFilter() {
        insertPolymorph("Good morning");
        long id = ContentUris.parseId(insertPolymorph("Good evening"));
        insertPolymorph("Goodnight");

        Cursor cursor = resolver.query(
                polymorphUri, null,
                Polymorph.VALUE + "=?", new String[]{"Good evening"}, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(id, cursor.getLong(0));
        cursor.close();
    }

    public void test_query_canSort() {
        long id2 = ContentUris.parseId(insertPolymorph(2));
        long id1 = ContentUris.parseId(insertPolymorph(1));
        Cursor cursor;

        cursor = resolver.query(polymorphUri, ID_PROJECTION, null, null, Polymorph._CREATED_AT + " ASC");
        cursor.moveToFirst();
        assertEquals(id2, cursor.getLong(0));
        cursor.close();

        cursor = resolver.query(polymorphUri, ID_PROJECTION, null, null, Polymorph.VALUE + " ASC");
        cursor.moveToFirst();
        assertEquals(id1, cursor.getLong(0));
        cursor.close();
    }

    public void test_insert() {
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Hello World!");

        Uri uri = resolver.insert(ModelManager.getContentUri(Polymorph.class), values);
        assertEquals(ModelManager.getContentItemType(Polymorph.class), resolver.getType(uri));

        cursor = queryPolymorphs();
        assertTrue(cursor.moveToFirst());
        assertEquals("Hello World!", cursor.getString(cursor.getColumnIndex(Polymorph.VALUE)));
    }

    public void test_insert_handlesItemUris() {
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Hello");
        Uri uri = Uri.withAppendedPath(polymorphUri, "1");

        try {
            resolver.insert(uri, values);
            assert false;
        }
        catch (IllegalArgumentException e) {}
    }

    public void test_update() {
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Bad");

        resolver.insert(polymorphUri, values);

        values.put(Polymorph.VALUE, "Good");
        assertEquals(1, resolver.update(polymorphUri, values, null, null));

        Cursor cursor = resolver.query(
                polymorphUri, new String[]{Polymorph.VALUE}, null, null, null);
        cursor.moveToFirst();
        assertEquals("Good", cursor.getString(0));
    }

    public void test_update_canUpdateMany() {
        insertPolymorph("Bad");
        insertPolymorph("Bad");
        insertPolymorph("Okay");

        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Good");
        assertEquals(2, getProvider().update(
                polymorphUri, values, Polymorph.VALUE + "=?", new String[]{"Bad"}));

        Cursor cursor = resolver.query(
                polymorphUri, VALUE_PROJECTION, null, null, Polymorph._CREATED_AT + " ASC");
        assertEquals(3, cursor.getCount());
        cursor.moveToFirst();
        assertEquals("Good", cursor.getString(0));
        cursor.moveToNext();
        assertEquals("Good", cursor.getString(0));
        cursor.moveToNext();
        assertEquals("Okay", cursor.getString(0));
    }

    public void test_update_handlesItemUris() {
        Uri hiUri = insertPolymorph("Hello");
        Uri byeUri = insertPolymorph("Hello");

        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Goodbye");
        assertEquals(1, resolver.update(byeUri, values, null, null));

        Cursor cursor;
        cursor = resolver.query(hiUri, VALUE_PROJECTION, null, null, null);
        cursor.moveToFirst();
        assertEquals("Hello", cursor.getString(0));
        cursor = resolver.query(byeUri, VALUE_PROJECTION, null, null, null);
        cursor.moveToFirst();
        assertEquals("Goodbye", cursor.getString(0));
    }

    public void test_delete() {
        insertPolymorph("Hello");

        assertEquals(1, resolver.delete(polymorphUri, null, null));

        Cursor cursor = resolver.query(polymorphUri, null, null, null, null);
        assertEquals(0, cursor.getCount());
    }

    public void test_delete_handlesItemUris() {
        long keepId = ContentUris.parseId(insertPolymorph("Hello"));
        long delId  = ContentUris.parseId(insertPolymorph("Hola"));

        assertEquals(1, resolver.delete(ContentUris.withAppendedId(polymorphUri, delId), null, null));

        Cursor cursor;
        cursor = resolver.query(polymorphUri, ID_PROJECTION, null, null, null);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(keepId, cursor.getLong(0));
    }

}
