package net.xaethos.lib.calico.integration.tests;

import junit.framework.AssertionFailedError;

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

    public static <T extends Throwable> void assertThrows(Class<T> expectedThrowable, Runnable action) throws Throwable {
        try {
            action.run();
            fail(String.format("Expected %s but nothing was thrown",
                    expectedThrowable.getSimpleName()));
        }
        catch (AssertionFailedError e) {
            throw e;
        }
        catch (Throwable throwable) {
            if (!expectedThrowable.isInstance(throwable)) {
                fail(String.format("Expected %s but %s was thrown",
                        expectedThrowable.getSimpleName(),
                        throwable.getClass().getSimpleName()));
            }
        }
    }

}
