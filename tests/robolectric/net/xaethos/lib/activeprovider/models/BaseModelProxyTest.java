package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BaseModelProxyTest {

    private static class TestModelProxy extends BaseModelProxy {
        @Override
        public Object get(String field) {
            return null;
        }
    }

    @Model
    private static interface TestModel {
        public Integer badMethod();

        public static final String COL_INT = "int";
        @Getter(COL_INT) public Integer getInt();
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
        assertThat(iface, is(not(nullValue())));
        iface.getInt();
        verify(mock).get(TestModel.COL_INT);
    }

}
