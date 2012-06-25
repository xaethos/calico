package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.annotations.Setter;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ModelHandlerTest {

    public class ROHandler<T extends Model> extends ModelHandler<T> implements ReadOnlyModelHandler {
        public ROHandler(Class<T> modelInterface) {
            super(modelInterface);
        }

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
        @Override public T       writableCopy()             { return null; }
    }

    public class RWHandler<T extends Model> extends ROHandler<T> implements ReadWriteModelHandler {
        public RWHandler(Class<T> modelInterface) {
            super(modelInterface);
        }

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

    @ModelInfo(authority = "com.example.content", tableName = "table", contentType = "vnd.example.table")
    private static interface TestModel extends Model {
        public Integer badMethod();

        public static final String INT = "int";
        @Getter(INT) public Integer getInt();
        @Setter(INT) public void setInt(Integer i);
    }

    private static interface BadInterface extends Model {}

    RWHandler<TestModel> handler;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotHandleUnannotatedInterfaces() {
        new RWHandler<BadInterface>(BadInterface.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotHandleUnsupportedMethods() {
        handler = new RWHandler<TestModel>(TestModel.class);
        handler.getModelProxy().badMethod();
    }

    @Test
    public void shouldForwardGetterMethodsToProperGetter() {
        handler = new RWHandler<TestModel>(TestModel.class);
        RWHandler<TestModel> mock = spy(handler);
        TestModel model = mock.getModelProxy();
        model.getInt();
        verify(mock).getInteger(TestModel.INT);
    }

    @Test
    public void shouldForwardSetterMethodsToSet() {
        handler = new RWHandler<TestModel>(TestModel.class);
        RWHandler<TestModel> mock = spy(handler);
        TestModel model = mock.getModelProxy();
        model.setInt(42);
        verify(mock).set(TestModel.INT, 42);
    }

    @Test
    public void testGetGetterName() throws Throwable {
        assertThat(ModelHandler.getGetterName(String.class), is("getString"));
        assertThat(ModelHandler.getGetterName(byte[].class), is("getbyteArray"));
        assertThat(ModelHandler.getGetterName(RWHandler.class), is("getRWHandler"));
    }

    ////////// Model tests //////////

    @Test
    public void isReadOnly() {
        assertThat(new ROHandler<TestModel>(TestModel.class).isReadOnly(), is(true));
        assertThat(new RWHandler<TestModel>(TestModel.class).isReadOnly(), is(false));
    }

}
