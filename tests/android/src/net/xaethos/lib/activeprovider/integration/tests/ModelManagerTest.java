package net.xaethos.lib.activeprovider.integration.tests;

import android.content.ContentUris;
import android.net.Uri;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.Model;
import net.xaethos.lib.activeprovider.models.ModelManager;

import static net.xaethos.lib.activeprovider.integration.tests.Assert.assertThrows;

public class ModelManagerTest extends BaseProviderTest {

    ModelInfo dataInfo;
    ModelManager manager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        manager = new ModelManager(getMockContext());
        dataInfo = Polymorph.class.getAnnotation(ModelInfo.class);
    }

    ////////// Static method tests //////////

    public void test_getModelInfo() {
        assertEquals(dataInfo, ModelManager.getModelInfo(Polymorph.class));
    }

    public void test_getModelInfo_shouldThrowException() throws Throwable {
        assertThrows(IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                ModelManager.getModelInfo(NotAnnotated.class);
            }
        });
    }

    public void test_getContentUri() throws Exception {
        Uri uri = Uri.parse("content://net.xaethos.lib.activeprovider.integration/polymorphs");
        assertEquals(uri, ModelManager.getContentUri(dataInfo));
        assertEquals(uri, ModelManager.getContentUri(Polymorph.class));

        uri = ContentUris.withAppendedId(uri, 42L);
        assertEquals(uri, ModelManager.getContentUri(dataInfo, 42L));
        assertEquals(uri, ModelManager.getContentUri(Polymorph.class, 42L));
    }

    public void test_getContentDirType() {
        String mimeType = "vnd.android.cursor.dir/vnd.xaethos.test.polymorph";
        assertEquals(mimeType, ModelManager.getContentDirType(dataInfo));
        assertEquals(mimeType, ModelManager.getContentDirType(Polymorph.class));
    }

    public void test_getContentItemType() {
        String mimeType = "vnd.android.cursor.item/vnd.xaethos.test.polymorph";
        assertEquals(mimeType, ModelManager.getContentItemType(dataInfo));
        assertEquals(mimeType, ModelManager.getContentItemType(Polymorph.class));
    }

    ////////// Instance method tests //////////

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

    public void test_create() {
        Polymorph poly = manager.create(Polymorph.class);
        assertNotNull(poly);
        assertNull(poly.getIntegerValue());
        assertFalse(poly.isReadOnly());
    }

    public void test_fetch_model() {
        Uri uri = insertPolymorph("dining table");

        Polymorph poly = manager.fetch(Polymorph.class, ContentUris.parseId(uri));

        assertNotNull(poly);
        assertEquals("dining table", poly.getStringValue());
        assertFalse(poly.isReadOnly());
    }

    public void test_save_updatesInsertsNewModels() {
        Polymorph poly = manager.create(Polymorph.class);
        poly.setStringValue("desk");

        assertTrue(manager.save(poly));

        cursor = manager.query(Polymorph.class, new String[]{Polymorph.VALUE}, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertTrue(cursor.isLast());
        assertEquals("desk", cursor.getString(0));
    }

    public void test_save_updatesExistingModels() {
        Uri uri = insertPolymorph("dining table");

        Polymorph poly = manager.fetch(Polymorph.class, ContentUris.parseId(uri));
        poly.setStringValue("desk");

        assertTrue(manager.save(poly));

        poly = manager.fetch(Polymorph.class, ContentUris.parseId(uri));
        assertEquals("desk", poly.getStringValue());
    }

    public void test_delete() {
        Uri uri = insertPolymorph("dining table");

        Polymorph poly = manager.fetch(Polymorph.class, ContentUris.parseId(uri));

        assertTrue(manager.delete(poly));

        poly = manager.fetch(Polymorph.class, ContentUris.parseId(uri));
        assertNull(poly);
    }

    ////////// Helpers //////////

    private interface NotAnnotated extends Model {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private abstract class NotAnInterface implements Model {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private interface NotExtendingModel {}

}
