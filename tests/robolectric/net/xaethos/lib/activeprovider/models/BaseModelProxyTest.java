package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BaseModelProxyTest {

    private static class TestModelProxy extends BaseModelProxy {
        @Override
        public Object get(String field) {
            return null;
        }

        @Override
        public void set(String field, Object value) {
        }
    }

    @Model
    private static interface TestModel {
        public Integer badMethod();

        public static final String COL_INT = "int";
        @Getter(COL_INT) public Integer getInt();
        @Setter(COL_INT) public void setInt(Integer i);
    }

    private static interface BadInterface {}

    private TestModelProxy proxy;

    @Before
    public void mockProxy() {
        proxy = new TestModelProxy();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotHandleUnannotatedInterfaces() {
        BaseModelProxy.newModelInstance(BadInterface.class, proxy);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotHandleUnsupportedMethods() {
        TestModel iface = BaseModelProxy.newModelInstance(TestModel.class, proxy);
        iface.badMethod();
    }

    @Test
    public void shouldForwardGetterMethodsToGet() {
        TestModelProxy mock = spy(proxy);
        TestModel iface = BaseModelProxy.newModelInstance(TestModel.class, mock);
        iface.getInt();
        verify(mock).get(TestModel.COL_INT);
    }

    @Test
    public void shouldForwardSetterMethodsToSet() {
        TestModelProxy mock = spy(proxy);
        TestModel iface = BaseModelProxy.newModelInstance(TestModel.class, mock);
        iface.setInt(42);
        verify(mock).set(TestModel.COL_INT, 42);
    }

}
