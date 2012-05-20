package net.xaethos.lib.activeprovider.integration.tests;

import java.util.Arrays;
import java.util.Collection;

public class Assert extends junit.framework.Assert {

    private Assert(){}

    protected static <T> void assertHasItems(Collection<T> actual, T... expected) {
        for (T item : expected) {
            assertTrue(actual.toString() + " expected to contain " + item.toString(), actual.contains(item));
        }
    }

    public static <T> void assertHasItems(T[] actual, T... expected) {
        assertHasItems(Arrays.asList(actual), expected);
    }

}
