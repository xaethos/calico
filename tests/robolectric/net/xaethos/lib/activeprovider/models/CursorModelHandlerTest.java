package net.xaethos.lib.activeprovider.models;

import android.database.Cursor;
import com.example.fixtures.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CursorModelHandlerTest {

    Cursor cursor;
    CursorModelHandler handler;
    Data model;

    @Before
    public void instantiateHandler() {
        cursor = mock(Cursor.class);
        when(cursor.getColumnIndexOrThrow(anyString())).thenReturn(1);
        handler = new CursorModelHandler(cursor);
        model = CursorModelHandler.newModelInstance(Data.class, handler);
    }

    @Test
    public void testGetCursor() {
        assertThat(handler.getCursor(), is(sameInstance(cursor)));
    }

    @Test
    public void testGetString() throws Exception {
        when(cursor.getString(1)).thenReturn("foo");
        assertThat(model.getString(), is("foo"));
    }

    @Test
    public void testGetBoolean() throws Exception {
        when(cursor.getInt(1)).thenReturn(1);
        assertThat(model.getBool(), is(true));
    }

    @Test
    public void testGetByte() throws Exception {
        when(cursor.getShort(1)).thenReturn((short) 7);
        assertThat(model.getByte(), is((byte) 7));
    }

    @Test
    public void testGetShort() throws Exception {
        when(cursor.getShort(1)).thenReturn((short)8);
        assertThat(model.getShort(), is((short)8));
    }

    @Test
    public void testGetInteger() throws Exception {
        when(cursor.getInt(1)).thenReturn(25);
        assertThat(model.getInt(), is(25));
    }

    @Test
    public void testGetLong() throws Exception {
        when(cursor.getLong(1)).thenReturn(987654321L);
        assertThat(model.getLong(), is(987654321L));
    }

    @Test
    public void testGetFloat() throws Exception {
        when(cursor.getFloat(1)).thenReturn(1.1f);
        assertThat(model.getFloat(), is(1.1f));
    }

    @Test
    public void testGetDouble() throws Exception {
        when(cursor.getDouble(1)).thenReturn(2.2);
        assertThat(model.getDouble(), is(2.2));
    }

    @Test
    public void testGetBlob() throws Exception {
        byte[] data = { 1,2,3,4 };
        when(cursor.getBlob(1)).thenReturn(data);
        assertThat(model.getData(), is(data));
    }

    @Test
    public void testGetDate() throws Exception {
        long milliseconds = System.currentTimeMillis();
        Date date = new Date(milliseconds);
        when(cursor.getLong(1)).thenReturn(milliseconds);
        assertThat(model.getTimestamp(), is(date));
    }

}
