package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HashModelHandlerTest {

    @Model
    private static interface TestModel {

        public static final String NAME = "name";
        @Getter(NAME) public String getName();
        @Setter(NAME) public void setName(String s);

    }

    @Test
    public void shouldBeAbleToGetValuesFromSeededContent() {
        HashMap<String, Object> map = new HashMap<String, Object>(1);
        map.put(TestModel.NAME, "foobar");
        TestModel model = HashModelHandler.newModelInstance(TestModel.class, new HashModelHandler(map));

        assertThat(model.getName(), is("foobar"));
    }

    @Test
    public void shouldBeAbleToSetAndGetValues() {
        TestModel model = HashModelHandler.newModelInstance(TestModel.class, new HashModelHandler());

        model.setName("foobar");
        assertThat(model.getName(), is("foobar"));
    }

}
