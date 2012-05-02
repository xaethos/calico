package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ModelHandlerTest {

    public class ROModelHandler extends ModelHandler implements ReadableModelHandler {
        @Override public String  getString(String field)    { return null; }
        @Override public Boolean getBoolean(String field)   { return null; }
        @Override public Byte    getByte(String field)      { return null; }
        @Override public Short   getShort(String field)     { return null; }
        @Override public Integer getInteger(String field)   { return null; }
        @Override public Long    getLong(String field)      { return null; }
        @Override public Float   getFloat(String field)     { return null; }
        @Override public Double  getDouble(String field)    { return null; }
        @Override public byte[]  getbyteArray(String field) { return null; }
        @Override public Date    getDate(String field)      { return null; }
    }

    public class RWModelHandler extends ROModelHandler implements WritableModelHandler {
        @Override public void set(String field, String value)  {}
        @Override public void set(String field, Boolean value) {}
        @Override public void set(String field, Byte value)    {}
        @Override public void set(String field, Short value)   {}
        @Override public void set(String field, Integer value) {}
        @Override public void set(String field, Long value)    {}
        @Override public void set(String field, Float value)   {}
        @Override public void set(String field, Double value)  {}
        @Override public void set(String field, byte[] value)  {}
        @Override public void set(String field, Date value)  {}

        @Override public void setNull(String field) {}

    }

    @Model(authority = "com.example.content", tableName = "table", contentType = "vnd.example.table")
    private static interface TestModel {
        public Integer badMethod();

        public static final String INT = "int";
        @Getter(INT) public Integer getInt();
        @Setter(INT) public void setInt(Integer i);
    }

    private static interface BadInterface {}

    private RWModelHandler proxy;

    @Before
    public void mockProxy() {
        proxy = new RWModelHandler();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotHandleUnannotatedInterfaces() {
        ModelHandler.newModelInstance(BadInterface.class, proxy);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotHandleUnsupportedMethods() {
        TestModel model = ModelHandler.newModelInstance(TestModel.class, proxy);
        model.badMethod();
    }

    @Test
    public void shouldForwardGetterMethodsToProperGetter() {
        RWModelHandler mock = spy(proxy);
        TestModel model = ModelHandler.newModelInstance(TestModel.class, mock);
        model.getInt();
        verify(mock).getInteger(TestModel.INT);
    }

    @Test
    public void shouldForwardSetterMethodsToSet() {
        RWModelHandler mock = spy(proxy);
        TestModel model = ModelHandler.newModelInstance(TestModel.class, mock);
        model.setInt(42);
        verify(mock).set(TestModel.INT, 42);
    }

    @Test
    public void testGetGetterName() throws Throwable {
        assertThat(ModelHandler.getGetterName(String.class), is("getString"));
        assertThat(ModelHandler.getGetterName(byte[].class), is("getbyteArray"));
        assertThat(ModelHandler.getGetterName(RWModelHandler.class), is("getRWModelHandler"));
    }

    @Test
    public void testIsReadable() {
        assertThat(new ModelHandler().isReadable(), is(false));
        assertThat(new ROModelHandler().isReadable(), is(true));
        assertThat(new RWModelHandler().isReadable(), is(true));
    }

    @Test
    public void testIsWritable() {
        assertThat(new ModelHandler().isWritable(), is(false));
        assertThat(new ROModelHandler().isWritable(), is(false));
        assertThat(new RWModelHandler().isWritable(), is(true));
    }

}
