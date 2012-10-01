package net.xaethos.lib.calico.integration.tests;

import android.content.ContentValues;
import net.xaethos.app.calicosample.models.Polymorph;
import net.xaethos.lib.calico.models.ModelManager;

import java.util.List;

public class ModelCursorTest extends BaseProviderTest {
    private static final String[] TABLES = {
            "dining table",
            "desk",
            "coffee table",
    };
    private static final String[] PROJ = {
            Polymorph._ID,
            Polymorph.VALUE
    };

    @Override
    public void setUp() throws Exception {
        super.setUp();

        for(String table : TABLES) insertPolymorph(table);

        cursor = new ModelManager(getMockContext()).query(Polymorph.class, PROJ, null, null, null);
    }

    ////////// Tests //////////

    public void test_getList() {
        List<Polymorph> list = cursor.getList();
        assertNotNull(list);
        assertEquals(cursor.getCount(), list.size());
        for (int i=0; i<cursor.getCount(); ++i) {
            cursor.moveToPosition(i);
            assertEquals(cursor.getString(1), list.get(i).getStringValue());
        }
    }

    public void test_getModel() {
        for (int i=0; i<cursor.getCount(); ++i) {
            cursor.moveToPosition(i);
            Polymorph poly = cursor.getModel();

            assertEquals(cursor.getLong(0), (long)poly.getId());
            assertEquals(cursor.getString(1), poly.getStringValue());
            assertNull(poly.getCreatedAt());
            assertFalse(poly.isReadOnly());
        }
    }

    public void test_getValues() {
        for (int i=0; i<cursor.getCount(); ++i) {
            cursor.moveToPosition(i);
            ContentValues values = cursor.getValues();

            assertEquals(cursor.getLong(0), (long)values.getAsLong(Polymorph._ID));
            assertEquals(cursor.getString(1), values.getAsString(Polymorph.VALUE));
            assertFalse(values.containsKey(Polymorph._CREATED_AT));
        }
    }

    public void testIterable() {
        int i = 0;
        for (Polymorph polymorph : cursor) {
            assertEquals(TABLES[i], polymorph.getStringValue());
            ++i;
        }
        assertEquals(TABLES.length, i);
    }

}
