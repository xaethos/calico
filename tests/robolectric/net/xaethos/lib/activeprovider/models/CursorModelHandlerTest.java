package net.xaethos.lib.activeprovider.models;

import android.database.Cursor;
import android.database.MatrixCursor;
import com.example.fixtures.Data;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class CursorModelHandlerTest {

    Cursor cursor;
    CursorModelHandler<Data> handler;
    Data model;

    @Before public void instantiateHandler() {
        cursor = mock(Cursor.class);
        when(cursor.getColumnIndexOrThrow(anyString())).thenReturn(1);
        handler = new CursorModelHandler<Data>(Data.class, cursor);
        model = handler.getModelProxy();
    }

    @Test public void getCursor() {
        assertThat(handler.getCursor(), is(sameInstance(cursor)));
    }

    ////////// ReadOnlyModelHandler tests //////////

    @Test public void getString() throws Exception {
        when(cursor.getString(1)).thenReturn("foo");
        assertThat(model.getString(), is("foo"));
    }

    @Test public void getBoolean() throws Exception {
        when(cursor.getInt(1)).thenReturn(1);
        assertThat(model.getBool(), is(true));
    }

    @Test public void getByte() throws Exception {
        when(cursor.getShort(1)).thenReturn((short) 7);
        assertThat(model.getByte(), is((byte) 7));
    }

    @Test public void getShort() throws Exception {
        when(cursor.getShort(1)).thenReturn((short)8);
        assertThat(model.getShort(), is((short)8));
    }

    @Test public void getInteger() throws Exception {
        when(cursor.getInt(1)).thenReturn(25);
        assertThat(model.getInt(), is(25));
    }

    @Test public void getLong() throws Exception {
        when(cursor.getLong(1)).thenReturn(987654321L);
        assertThat(model.getLong(), is(987654321L));
    }

    @Test public void getFloat() throws Exception {
        when(cursor.getFloat(1)).thenReturn(1.1f);
        assertThat(model.getFloat(), is(1.1f));
    }

    @Test public void getDouble() throws Exception {
        when(cursor.getDouble(1)).thenReturn(2.2);
        assertThat(model.getDouble(), is(2.2));
    }

    @Test public void getBlob() throws Exception {
        byte[] data = { 1,2,3,4 };
        when(cursor.getBlob(1)).thenReturn(data);
        assertThat(model.getData(), is(data));
    }

    @Test public void getDate() throws Exception {
        long milliseconds = System.currentTimeMillis();
        Date date = new Date(milliseconds);
        when(cursor.getLong(1)).thenReturn(milliseconds);
        assertThat(model.getTimestamp(), is(date));
    }

    ////////// Shared behaviors //////////

    public static class ActiveModelUtils
            extends ActiveModelUtilsTest<CursorModelHandler<Data>> {
        @Override
        protected CursorModelHandler<Data> newHandler() {
            MatrixCursor cursor = new MatrixCursor(new String[]{
                    Data._ID,
                    Data.STRING,
                    Data.INT
            }, 1);
            cursor.addRow(new Object[]{ID, STRING, null});
            cursor.moveToFirst();
            return new CursorModelHandler<Data>(Data.class, cursor);
        }
    }

}
