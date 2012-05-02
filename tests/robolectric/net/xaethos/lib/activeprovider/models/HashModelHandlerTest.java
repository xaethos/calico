package net.xaethos.lib.activeprovider.models;

import com.example.fixtures.Data;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HashModelHandlerTest {

    @Test
    public void shouldBeAbleToGetValuesFromSeededContent() {
        HashMap<String, Object> map = new HashMap<String, Object>(1);
        map.put(Data.FOO, "foo");
        Data model = HashModelHandler.newModelInstance(Data.class, new HashModelHandler(map));

        assertThat(model.getFoo(), is("foobar"));
    }

    @Test
    public void shouldBeAbleToSetAndGetValues() {
        Data model = HashModelHandler.newModelInstance(Data.class, new HashModelHandler());

        model.setFoo("foobar");
        assertThat(model.getFoo(), is("foobar"));
    }

}
