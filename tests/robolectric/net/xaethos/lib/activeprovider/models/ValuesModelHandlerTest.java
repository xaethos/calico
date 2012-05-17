package net.xaethos.lib.activeprovider.models;

import android.content.ContentValues;
import com.example.fixtures.Data;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ValuesModelHandlerTest {

    Data model;
    ContentValues values;

    @Before
    public void instantiateModel() {
        ValuesModelHandler<Data> handler = new ValuesModelHandler<Data>(Data.class);
        model = handler.getModelProxy();
        values = handler.getValues();
    }

    @Test
    public void shouldBeAbleToGetValuesFromSeededContent() {
        ContentValues values = new ContentValues(1);
        values.put(Data.STRING, "foo");
        model = new ValuesModelHandler<Data>(Data.class, values).getModelProxy();

        assertThat(model.getString(), is("foo"));

        values.put(Data.STRING, "bar");
        assertThat(model.getString(), is("foo"));
    }

    ////////// ReadOnlyModelHandler tests //////////

    @Test
    public void testGetString() throws Exception {
        values.put(Data.STRING, "foo");
        assertThat(model.getString(), is("foo"));
    }
    @Test
    public void testSetString() throws Exception {
        model.setString("foo");
        assertThat(model.getString(), is("foo"));
    }

    @Test
    public void testGetBoolean() throws Exception {
        values.put(Data.BOOL, true);
        assertThat(model.getBool(), is(true));
    }
    @Test
    public void testSetBoolean() throws Exception {
        model.setBool(true);
        assertThat(model.getBool(), is(true));
    }

    @Test
    public void testGetByte() throws Exception {
        values.put(Data.BYTE, (byte) 7);
        assertThat(model.getByte(), is((byte) 7));
    }
    @Test
    public void testSetByte() throws Exception {
        model.setByte((byte) 7);
        assertThat(model.getByte(), is((byte) 7));
    }

    @Test
    public void testGetShort() throws Exception {
        values.put(Data.SHORT, (short) 8);
        assertThat(model.getShort(), is((short) 8));
    }
    @Test
    public void testSetShort() throws Exception {
        model.setShort((short) 8);
        assertThat(model.getShort(), is((short) 8));
    }

    @Test
    public void testGetInteger() throws Exception {
        values.put(Data.INT, 25);
        assertThat(model.getInt(), is(25));
    }
    @Test
    public void testSetInteger() throws Exception {
        model.setInt(25);
        assertThat(model.getInt(), is(25));
    }

    @Test
    public void testGetLong() throws Exception {
        values.put(Data.LONG, 987654321L);
        assertThat(model.getLong(), is(987654321L));
    }
    @Test
    public void testSetLong() throws Exception {
        model.setLong(987654321L);
        assertThat(model.getLong(), is(987654321L));
    }

    @Test
    public void testGetFloat() throws Exception {
        values.put(Data.FLOAT, 1.1f);
        assertThat(model.getFloat(), is(1.1f));
    }
    @Test
    public void testSetFloat() throws Exception {
        model.setFloat(1.1f);
        assertThat(model.getFloat(), is(1.1f));
    }

    @Test
    public void testGetDouble() throws Exception {
        values.put(Data.DOUBLE, 2.2);
        assertThat(model.getDouble(), is(2.2));
    }
    @Test
    public void testSetDouble() throws Exception {
        model.setDouble(2.2);
        assertThat(model.getDouble(), is(2.2));
    }

    @Test
    public void testGetBlob() throws Exception {
        byte[] data = { 1,2,3,4 };
        values.put(Data.DATA, data);
        assertThat(model.getData(), is(data));
    }
    @Test
    public void testSetBlob() throws Exception {
        byte[] data = { 1,2,3,4 };
        model.setData(data);
        assertThat(model.getData(), is(data));
    }

    @Test
    public void testGetDate() throws Exception {
        long milliseconds = System.currentTimeMillis();
        Date date = new Date(milliseconds);
        values.put(Data.DATE, milliseconds);
        assertThat(model.getTimestamp(), is(date));
    }
    @Test
    public void testSetDate() throws Exception {
        Date date = new Date();
        model.setTimestamp(date);
        assertThat(model.getTimestamp(), is(date));
    }

    ////////// Shared behavior tests //////////

    public static class ActiveModelUtils
            extends ActiveModelUtilsTest<ValuesModelHandler<Data>> {

        @Override
        protected ValuesModelHandler<Data> newHandler() {
            ContentValues values = new ContentValues(3);
            values.put(Data._ID, ID);
            values.put(Data.STRING, STRING);
            values.putNull(Data.INT);
            return new ValuesModelHandler<Data>(Data.class, values);
        }
    }

}
