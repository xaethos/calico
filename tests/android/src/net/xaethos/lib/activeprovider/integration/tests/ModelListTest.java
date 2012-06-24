package net.xaethos.lib.activeprovider.integration.tests;

import net.xaethos.lib.activeprovider.models.ModelManager;
import net.xaethos.lib.activeprovider.integration.models.Polymorph;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static net.xaethos.lib.activeprovider.integration.tests.Assert.assertThrows;

public class ModelListTest extends BaseProviderTest {
    private static final String[] TABLES = {
            "dining table",
            "desk",
            "coffee table",
    };

    List<Polymorph> polymorphs;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        for(String table : TABLES) insertPolymorph(table);

        cursor = new ModelManager(getMockContext()).query(Polymorph.class, null, null, null, null);
        polymorphs = cursor.getList();
    }

    ////////// Tests //////////

    public void test_get() {
        Polymorph diningTable = polymorphs.get(0);
        Polymorph coffeeTable = polymorphs.get(2);

        assertEquals("dining table", diningTable.getStringValue());
        assertEquals("coffee table", coffeeTable.getStringValue());

        for (Polymorph table : new Polymorph[]{diningTable, coffeeTable}) {
            assertFalse(table.isReadOnly());
        }
    }

    public void test_get_oob() throws Throwable {
        assertThrows(IndexOutOfBoundsException.class, new Runnable() {
            @Override public void run() { polymorphs.get(-1); }
        });
        assertThrows(IndexOutOfBoundsException.class, new Runnable() {
            @Override public void run() { polymorphs.get(polymorphs.size()); }
        });
    }

    public void test_size() {
        assertEquals(3, polymorphs.size());
    }

    public void testImmutable() throws Throwable {
        final Polymorph poly = polymorphs.get(0);

        assertThrows(UnsupportedOperationException.class, new Runnable() {
            @Override public void run() { polymorphs.add(poly); }
        });
        assertThrows(UnsupportedOperationException.class, new Runnable() {
            @Override public void run() { polymorphs.addAll(polymorphs); }
        });
        assertThrows(UnsupportedOperationException.class, new Runnable() {
            @Override public void run() { polymorphs.clear(); }
        });
        assertThrows(UnsupportedOperationException.class, new Runnable() {
            @Override public void run() { polymorphs.remove(poly); }
        });
        assertThrows(UnsupportedOperationException.class, new Runnable() {
            @Override public void run() { polymorphs.removeAll(polymorphs); }
        });
        assertThrows(UnsupportedOperationException.class, new Runnable() {
            @Override public void run() { polymorphs.retainAll(polymorphs); }
        });
    }

    public void test_iterator() throws Throwable {
        final Iterator<Polymorph> iterator = polymorphs.iterator();
        assertNotNull(iterator);

        for (String table : TABLES) {
            assertTrue(iterator.hasNext());
            Polymorph poly = iterator.next();
            assertThrows(UnsupportedOperationException.class, new Runnable() {
                @Override public void run() { iterator.remove(); }
            });
            assertFalse(poly.isReadOnly());
            assertEquals(table, poly.getStringValue());
        }

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, new Runnable() {
            @Override public void run() { iterator.next(); }
        });
    }

}
