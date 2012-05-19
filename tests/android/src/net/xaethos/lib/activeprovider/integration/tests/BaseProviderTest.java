package net.xaethos.lib.activeprovider.integration.tests;

import android.content.ContentValues;
import android.net.Uri;
import android.test.ProviderTestCase2;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.content.ActiveResolver;
import net.xaethos.lib.activeprovider.integration.MyProvider;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;
import net.xaethos.lib.activeprovider.models.ActiveModel;

import java.util.Arrays;
import java.util.Collection;

public class BaseProviderTest extends ProviderTestCase2<MyProvider> {

    static final String[] ID_PROJECTION = { Polymorph._ID };
    static final String[] VALUE_PROJECTION = { Polymorph.VALUE };

    ProviderInfo providerInfo;

    ActiveResolver.Cursor<Polymorph> cursor;
    Polymorph polymorph;
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

    ////////// Helper methods //////////

    protected ActiveResolver.Cursor<Polymorph> queryPolymorphs() {
        return new ActiveResolver.Cursor<Polymorph>(Polymorph.class, getMockContentResolver().query(
                ActiveModel.getContentUri(Polymorph.class), null, null, null, null));
    }

    protected Uri insertPolymorph(String value) {
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

    protected <T> void assertHasItems(Collection<T> actual, T... expected) {
        for (T item : expected) {
            assertTrue(actual.toString() + " expected to contain " + item.toString(), actual.contains(item));
        }
    }

    protected <T> void assertHasItems(T[] actual, T... expected) {
        assertHasItems(Arrays.asList(actual), expected);
    }

}
