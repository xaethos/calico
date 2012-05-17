package net.xaethos.lib.activeprovider.models;

import com.example.fixtures.Data;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


/**
 * Shared tests for ModelHandler implementations
 */
@RunWith(RobolectricTestRunner.class)
public abstract class ActiveModelUtilsTest<T extends ModelHandler<Data>> {

    protected static final long ID = 42;
    protected static final String STRING = "foobar";

    T handler;

    protected abstract T newHandler();

    @Before
    public void instantiateHandler() {
        handler = newHandler();
    }

    @Test
    public void validateTestData() {
        Data model = handler.getModelProxy();
        assertThat(model.getId(), is(ID));
        assertThat(model.getString(), is(STRING));
        assertThat(model.getInt(), is(nullValue()));
    }

    @Test
    public void writableCopy_isWritable() {
        assertFalse(handler.writableCopy().isReadOnly());
    }

    @Test
    public void writableCopy_isACopy() {
        Data model = handler.getModelProxy();
        assertThat((Data)model.writableCopy(), is(not(sameInstance(model))));
    }

}
