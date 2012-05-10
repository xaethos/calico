package net.xaethos.lib.activeprovider.content;

import android.content.ContentValues;
import android.net.Uri;
import com.example.fixtures.Data;
import com.example.fixtures.DataProvider;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContentResolver;
import net.xaethos.lib.activeprovider.models.ModelManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class ActiveResolverTest {

    protected static final String[] FURNITURE = {"coffee table", "desk", "table"};

    ActiveResolver resolver;

    @Before public void setUp() {
        resolver = new ActiveResolver(Robolectric.application);
        setUpProvider(resolver);
    }

    @Test public void query_forModelCursor() {
        ActiveResolver resolverSpy = spy(resolver);

        String[] projection = {Data._ID, Data.STRING};
        String   where      = Data.STRING + " LIKE ?";
        String[] whereArgs  = {"table"};
        String   orderBy    = Data.STRING;

        assertThat(resolverSpy.query(Data.class, projection, where, whereArgs, orderBy),
                is(notNullValue()));
        verify(resolverSpy).query(
                ModelManager.getContentUri(Data.class), projection, where, whereArgs, orderBy);
    }

    @RunWith(RobolectricTestRunner.class)
    public static class Cursor {

        ActiveResolver resolver;
        ActiveResolver.Cursor<Data> cursor;

        @Before public void setUp() {
            resolver = new ActiveResolver(Robolectric.application);
            setUpProvider(resolver);

            String[] projection = {Data._ID, Data.STRING};
            cursor = new ActiveResolver.Cursor<Data>(Data.class, resolver.query(
                    ModelManager.getContentUri(Data.class), projection, null, null, Data.STRING));
        }

        @Test public void constructor() {
            assertThat(cursor, is(notNullValue()));
            assertThat(cursor.getCount(), is(FURNITURE.length));
        }

        @Test public void getModel_isNullWhenCursorOOB() {
            cursor.moveToPosition(-1);
            assertThat(cursor.getModel(), is(nullValue()));
            cursor.moveToPosition(cursor.getCount());
            assertThat(cursor.getModel(), is(nullValue()));
        }

        @Test public void getModel_isNotNullWhenCursorInBounds() {
            cursor.moveToFirst();
            assertThat(cursor.getModel(), is(notNullValue()));
        }

        @Test public void modelReflectsCursorPosition() {
            cursor.moveToFirst();
            Data data = cursor.getModel();
            assertThat(data.getString(), is("coffee table"));
            cursor.moveToLast();
            assertThat(data.getString(), is("table"));
        }

    }

    protected static void setUpProvider(ActiveResolver resolver) {
        ActiveProvider provider = new DataProvider();
        provider.onCreate();

        ShadowContentResolver.registerProvider(
                ModelManager.getModelInfo(Data.class).authority(), provider);

        Uri uri = ModelManager.getContentUri(Data.class);
        ContentValues values = new ContentValues(1);
        for (String s : FURNITURE) {
            values.put(Data.STRING, s);
            resolver.insert(uri, values);
        }

    }

}
