package net.xaethos.lib.activeprovider.integration.tests;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.test.ProviderTestCase2;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.content.ActiveProvider;
import net.xaethos.lib.activeprovider.content.ActiveResolver;
import net.xaethos.lib.activeprovider.integration.MyProvider;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.integration.models.User;
import net.xaethos.lib.activeprovider.models.ActiveModel;

import java.util.Arrays;
import java.util.Collection;

public class ActiveProviderTest extends ProviderTestCase2<MyProvider> {

    static final String[] ID_PROJECTION = { Polymorph._ID };
    static final String[] VALUE_PROJECTION = { Polymorph.VALUE };

    ActiveProvider provider;
    ProviderInfo providerInfo;

    ModelInfo polymorphInfo;
    Uri polymorphUri;

    public ActiveProviderTest() {
        super(MyProvider.class, MyProvider.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        provider = getProvider();
        providerInfo = MyProvider.class.getAnnotation(ProviderInfo.class);

        polymorphInfo = Polymorph.class.getAnnotation(ModelInfo.class);
        polymorphUri = Uri.parse("content://" + polymorphInfo.authority() + "/" + polymorphInfo.tableName());
    }

    ////////// Tests //////////

    public void test_getProviderInfo() {
        assertEquals(providerInfo, provider.getProviderInfo());
    }

    public void test_getModels() {
        ModelInfo[] models = provider.getModels();
        assertEquals(2, models.length);
        assertEquals(providerInfo.models()[0].getAnnotation(ModelInfo.class), models[0]);
        assertEquals(providerInfo.models()[1].getAnnotation(ModelInfo.class), models[1]);
    }

    public void test_getType() {
        String type = polymorphInfo.contentType();

        assertEquals("vnd.android.cursor.dir/" + type,
                provider.getType(polymorphUri));

        assertEquals("vnd.android.cursor.item/" + type,
                provider.getType(Uri.withAppendedPath(polymorphUri, "1")));
    }

    public void test_query() throws Exception {
        ActiveResolver.Cursor cursor = query(User.class);
        assertNotNull(cursor);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void test_query_handlesProjections() throws Exception {
        Cursor cursor;
        String[] projection = {
                Polymorph._ID,
                Polymorph.VALUE,
        };

        cursor = provider.query(polymorphUri, projection, null, null, null);
        assertEquals(projection.length, cursor.getColumnCount());
        assertHasItems(projection, cursor.getColumnNames());

        cursor = provider.query(polymorphUri, ID_PROJECTION, null, null, null);
        assertEquals(1, cursor.getColumnCount());
        assertEquals(BaseColumns._ID, cursor.getColumnNames()[0]);
    }

    public void test_query_handlesDirAndItemUris() {
        Cursor cursor;
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Hello");

        Uri itemUri = provider.insert(polymorphUri, values);
        provider.insert(polymorphUri, values);

        cursor = provider.query(polymorphUri, ID_PROJECTION, null, null, null);
        assertEquals(2, cursor.getCount());

        cursor = provider.query(itemUri, ID_PROJECTION, null, null, null);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(ContentUris.parseId(itemUri), cursor.getLong(0));
    }

    public void test_query_canFilter() {
        makeSimpleData("Good morning");
        long id = makeSimpleData("Good evening");
        makeSimpleData("Goodnight");

        Cursor cursor = provider.query(
                polymorphUri, null,
                Polymorph.VALUE + "=?", new String[]{"Good evening"}, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(id, cursor.getLong(0));
    }

    public void test_query_canSort() {
        long idA = makeSimpleData("B");
        long idB = makeSimpleData("A");
        Cursor cursor;

        cursor = provider.query(polymorphUri, ID_PROJECTION, null, null, Polymorph._CREATED_AT + " ASC");
        cursor.moveToFirst();
        assertEquals(idA, cursor.getLong(0));

        cursor = provider.query(polymorphUri, ID_PROJECTION, null, null, Polymorph.VALUE + " ASC");
        cursor.moveToFirst();
        assertEquals(idB, cursor.getLong(0));
    }

    public void test_insert() {
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Hello World!");

        Uri uri = provider.insert(ActiveModel.getContentUri(Polymorph.class), values);
        assertEquals(ActiveModel.getContentItemType(Polymorph.class), provider.getType(uri));

        Cursor cursor = provider.query(uri, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals("Hello World!", cursor.getString(cursor.getColumnIndex(Polymorph.VALUE)));
    }

    public void test_insert_handlesItemUris() {
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Hello");
        Uri uri = Uri.withAppendedPath(polymorphUri, "1");

        try {
            provider.insert(uri, values);
            assert false;
        }
        catch (IllegalArgumentException e) {}
    }

    public void test_update() {
        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Bad");

        provider.insert(polymorphUri, values);

        values.put(Polymorph.VALUE, "Good");
        assertEquals(1, provider.update(polymorphUri, values, null, null));

        Cursor cursor = provider.query(
                polymorphUri, new String[]{Polymorph.VALUE}, null, null, null);
        cursor.moveToFirst();
        assertEquals("Good", cursor.getString(0));
    }

    public void test_update_canUpdateMany() {
        makeSimpleData("Bad");
        makeSimpleData("Bad");
        makeSimpleData("Okay");

        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Good");
        assertEquals(2, provider.update(
                polymorphUri, values, Polymorph.VALUE + "=?", new String[]{"Bad"}));

        Cursor cursor = provider.query(
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
        Uri hiUri = ContentUris.withAppendedId(polymorphUri, makeSimpleData("Hello"));
        Uri byeUri = ContentUris.withAppendedId(polymorphUri, makeSimpleData("Hello"));

        ContentValues values = new ContentValues();
        values.put(Polymorph.VALUE, "Goodbye");
        assertEquals(1, provider.update(byeUri, values, null, null));

        Cursor cursor;
        cursor = provider.query(hiUri, VALUE_PROJECTION, null, null, null);
        cursor.moveToFirst();
        assertEquals("Hello", cursor.getString(0));
        cursor = provider.query(byeUri, VALUE_PROJECTION, null, null, null);
        cursor.moveToFirst();
        assertEquals("Goodbye", cursor.getString(0));
    }

    public void test_delete() {
        makeSimpleData("Hello");

        assertEquals(1, provider.delete(polymorphUri, null, null));

        Cursor cursor = provider.query(polymorphUri, null, null, null, null);
        assertEquals(0, cursor.getCount());
    }

    public void test_delete_handlesItemUris() {
        long keepId = makeSimpleData("Hello");
        long delId = makeSimpleData("Hola");

        assertEquals(1, provider.delete(ContentUris.withAppendedId(polymorphUri, delId), null, null));

        Cursor cursor;
        cursor = provider.query(polymorphUri, ID_PROJECTION, null, null, null);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(keepId, cursor.getLong(0));
    }

    ////////// Helper methods //////////

    private long makeSimpleData(ContentValues values) {
        Uri uri = provider.insert(polymorphUri, values);
        return ContentUris.parseId(uri);
    }

    private long makeSimpleData(String s) {
        ContentValues values = new ContentValues(1);
        values.put(Polymorph.VALUE, s);
        values.put(Polymorph._CREATED_AT, System.currentTimeMillis());
        values.put(Polymorph._UPDATED_AT, values.getAsLong(Polymorph._CREATED_AT));
        return makeSimpleData(values);
    }

    protected <T> void assertHasItems(Collection<T> actual, T... expected) {
        for (T item : expected) {
            assertTrue(actual.toString() + " expected to contain " + item.toString(), actual.contains(item));
        }
    }

    protected <T> void assertHasItems(T[] actual, T... expected) {
        assertHasItems(Arrays.asList(actual), expected);
    }

    protected <T extends ActiveModel.Base> ActiveResolver.Cursor<T> query(Class<T> cls) {
        return new ActiveResolver.Cursor<T>(cls, getMockContentResolver().query(
                ActiveModel.getContentUri(cls), null, null, null, null));
    }

}
