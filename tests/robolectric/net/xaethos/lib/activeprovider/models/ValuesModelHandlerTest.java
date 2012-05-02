package net.xaethos.lib.activeprovider.models;

import android.content.ContentValues;
import com.example.fixtures.Data;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ValuesModelHandlerTest {

    @Test
    public void shouldBeAbleToGetValuesFromSeededContent() {
        ContentValues values = new ContentValues(1);
        values.put(Data.FOO, "foo");
        Data model = ValuesModelHandler.newModelInstance(Data.class, new ValuesModelHandler(values));

        assertThat(model.getFoo(), is("foo"));
    }

    @Test
    public void shouldBeAbleToSetAndGetValues() {
        Data model = ValuesModelHandler.newModelInstance(Data.class, new ValuesModelHandler());

        model.setFoo("foo");
        assertThat(model.getFoo(), is("foo"));
    }

}
