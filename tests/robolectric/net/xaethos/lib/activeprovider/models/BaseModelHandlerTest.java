package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BaseModelHandlerTest {

    public class TestModelHandler extends BaseModelHandler {
        @Override public String  getString(String field)    { return null; }
        @Override public Boolean getBoolean(String field)   { return null; }
        @Override public Byte    getByte(String field)      { return null; }
        @Override public Short   getShort(String field)     { return null; }
        @Override public Integer getInteger(String field)   { return null; }
        @Override public Long    getLong(String field)      { return null; }
        @Override public Float   getFloat(String field)     { return null; }
        @Override public Double  getDouble(String field)    { return null; }
        @Override public byte[]  getbyteArray(String field) { return null; }

        @Override public void set(String field, String value)  {}
        @Override public void set(String field, Boolean value) {}
        @Override public void set(String field, Byte value)    {}
        @Override public void set(String field, Short value)   {}
        @Override public void set(String field, Integer value) {}
        @Override public void set(String field, Long value)    {}
        @Override public void set(String field, Float value)   {}
        @Override public void set(String field, Double value)  {}
        @Override public void set(String field, byte[] value)  {}

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

    private TestModelHandler proxy;

    @Before
    public void mockProxy() {
        proxy = new TestModelHandler();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotHandleUnannotatedInterfaces() {
        BaseModelHandler.newModelInstance(BadInterface.class, proxy);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotHandleUnsupportedMethods() {
        TestModel model = BaseModelHandler.newModelInstance(TestModel.class, proxy);
        model.badMethod();
    }

    @Test
    public void shouldForwardGetterMethodsToProperGetter() {
        TestModelHandler mock = spy(proxy);
        TestModel model = BaseModelHandler.newModelInstance(TestModel.class, mock);
        model.getInt();
        verify(mock).getInteger(TestModel.INT);
    }

    @Test
    public void shouldForwardSetterMethodsToSet() {
        TestModelHandler mock = spy(proxy);
        TestModel model = BaseModelHandler.newModelInstance(TestModel.class, mock);
        model.setInt(42);
        verify(mock).set(TestModel.INT, 42);
    }

    @Test
    public void testGetGetterName() throws Throwable {
        assertThat(BaseModelHandler.getGetterName(String.class), is("getString"));
        assertThat(BaseModelHandler.getGetterName(byte[].class), is("getbyteArray"));
        assertThat(BaseModelHandler.getGetterName(TestModelHandler.class), is("getTestModelHandler"));
    }

}
