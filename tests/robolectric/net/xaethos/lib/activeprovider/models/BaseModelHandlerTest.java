package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BaseModelHandlerTest {

    public static class TestModelHandler extends BaseModelHandler {
        @Override
        public Object get(String field, Class<?> cls) {
            return null;
        }

        @Override
        public void set(String field, Object value) {
        }
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
    public void shouldForwardGetterMethodsToGet() {
        TestModelHandler mock = spy(proxy);
        TestModel model = BaseModelHandler.newModelInstance(TestModel.class, mock);
        model.getInt();
        verify(mock).get(TestModel.INT, Integer.class);
    }

    @Test
    public void shouldForwardSetterMethodsToSet() {
        TestModelHandler mock = spy(proxy);
        TestModel model = BaseModelHandler.newModelInstance(TestModel.class, mock);
        model.setInt(42);
        verify(mock).set(TestModel.INT, 42);
    }

}
