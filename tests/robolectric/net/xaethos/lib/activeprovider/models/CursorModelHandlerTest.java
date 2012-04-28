package net.xaethos.lib.activeprovider.models;

import android.database.Cursor;
import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CursorModelHandlerTest {

    @Model
    private static interface TestModel {
        public static final String STRING  = "string";
        public static final String INTEGER = "integer";
        public static final String BOOLEAN = "boolean";
        public static final String SHORT   = "short";
        public static final String LONG    = "long";
        public static final String FLOAT   = "float";
        public static final String DOUBLE  = "double";
        public static final String BLOB    = "blob";

        @Getter(STRING) public String   getString();
        @Setter(STRING) public void     setString(String s);
        @Getter(INTEGER) public Integer getInteger();
        @Setter(INTEGER) public void    setInteger(Integer d);
        @Getter(BOOLEAN) public Boolean  isTrue();
        @Setter(BOOLEAN) public void    setTrue(Boolean flag);
        @Getter(SHORT) public Short     getShort();
        @Setter(SHORT) public void      setShort(Short sd);
        @Getter(LONG) public Long       getLong();
        @Setter(LONG) public void       setLong(Long ld);
        @Getter(FLOAT) public Float     getFloat();
        @Setter(FLOAT) public void      setFloat(Float f);
        @Getter(DOUBLE) public Double   getDouble();
        @Setter(DOUBLE) public void     setDouble(Double lf);
        @Getter(BLOB) public byte[]     getData();
        @Setter(BLOB) public void       setData(byte[] data);
    }

    Cursor cursor;
    CursorModelHandler handler;
    TestModel model;

    @Before
    public void instantiateHandler() {
        cursor = mock(Cursor.class);
        when(cursor.getColumnIndexOrThrow(anyString())).thenReturn(1);
        handler = new CursorModelHandler(cursor);
        model = CursorModelHandler.newModelInstance(TestModel.class, handler);
    }

    @Test
    public void testGetCursor() {
        assertThat(handler.getCursor(), is(sameInstance(cursor)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getShouldBalkAtUnhandledTypes() {
        handler.get("anything", Cursor.class);
    }

    @Test
    public void testGetString() throws Exception {
        when(cursor.getString(1)).thenReturn("foo");
        assertThat(model.getString(), is("foo"));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetString() throws Exception {
        model.setString("foo");
        assertThat(model.getString(), is("foo"));
    }

    @Test
    public void testGetInteger() throws Exception {
        when(cursor.getInt(1)).thenReturn(25);
        assertThat(model.getInteger(), is(25));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetInteger() throws Exception {
        model.setInteger(25);
        assertThat(model.getInteger(), is(25));
    }

    @Test
    public void testGetBoolean() throws Exception {
        when(cursor.getInt(1)).thenReturn(1);
        assertThat(model.isTrue(), is(true));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetBoolean() throws Exception {
        model.setTrue(true);
        assertThat(model.isTrue(), is(true));
    }

    @Test
    public void testGetShort() throws Exception {
        when(cursor.getShort(1)).thenReturn((short)8);
        assertThat(model.getShort(), is((short)8));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetShort() throws Exception {
        model.setShort((short) 8);
        assertThat(model.getShort(), is((short) 8));
    }

    @Test
    public void testGetLong() throws Exception {
        when(cursor.getLong(1)).thenReturn(987654321L);
        assertThat(model.getLong(), is(987654321L));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetLong() throws Exception {
        model.setLong(987654321L);
        assertThat(model.getLong(), is(987654321L));
    }

    @Test
    public void testGetFloat() throws Exception {
        when(cursor.getFloat(1)).thenReturn(1.1f);
        assertThat(model.getFloat(), is(1.1f));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetFloat() throws Exception {
        model.setFloat(1.1f);
        assertThat(model.getFloat(), is(1.1f));
    }

    @Test
    public void testGetDouble() throws Exception {
        when(cursor.getDouble(1)).thenReturn(2.2);
        assertThat(model.getDouble(), is(2.2));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetDouble() throws Exception {
        model.setDouble(2.2);
        assertThat(model.getDouble(), is(2.2));
    }

    @Test
    public void testGetBlob() throws Exception {
        byte[] data = { 1,2,3,4 };
        when(cursor.getBlob(1)).thenReturn(data);
        assertThat(model.getData(), is(data));
    }
    @Test(expected = UnsupportedOperationException.class)
    public void testSetBlob() throws Exception {
        byte[] data = { 1,2,3,4 };
        model.setData(data);
        assertThat(model.getData(), is(data));
    }

}
