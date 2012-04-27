package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Model;
import org.junit.Test;

public class BaseModelTest {

    @Model
    private static interface TestInterface {
        public int badMethod();
    }

    private static interface BadInterface {
        public int myInt();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotHandleUnannotatedInterfaces() {
        BaseModel.newModelInstance(BadInterface.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotHandleUnsupportedMethods() {
        TestInterface iface = BaseModel.newModelInstance(TestInterface.class);
        iface.badMethod();
    }

}
