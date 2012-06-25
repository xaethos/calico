package net.xaethos.lib.calico.integration.tests;

import android.R;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
import net.xaethos.lib.calico.integration.UsersActivity;
import net.xaethos.lib.calico.integration.models.User;
import net.xaethos.lib.calico.models.ModelManager;

public class ModelLoaderTest extends ActivityInstrumentationTestCase2<UsersActivity> {
    static final String[] NAMES = { "Pedro", "Juan", "Diego" };

    Uri usersUri;

    public ModelLoaderTest() {
        super(UsersActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        usersUri = ModelManager.getContentUri(User.class);
        getInstrumentation().getTargetContext().getContentResolver().delete(usersUri, null, null);
    }

    public void test_cursorReloading() throws Exception {
        final UsersActivity activity = getActivity();
        ContentResolver resolver = activity.getContentResolver();

        ContentValues values = new ContentValues(1);
        for (String name : NAMES) {
            values.put(User.NAME, name);
            resolver.insert(usersUri, values);
        }

        getInstrumentation().waitForIdle(new Runnable() {
            @Override
            public void run() {
                ListView lv = (ListView) activity.findViewById(R.id.list);
                assertEquals(NAMES.length, lv.getChildCount());
                for (String name : NAMES) {
                    TextView tv = (TextView) lv.getChildAt(0).findViewById(R.id.text1);
                    assertEquals(name, tv.getText());
                }
            }
        });

        values.put(User.NAME, "Matias");
        resolver.insert(usersUri, values);

        getInstrumentation().waitForIdle(new Runnable() {
            @Override
            public void run() {
                ListView lv = (ListView) activity.findViewById(R.id.list);
                assertEquals(NAMES.length + 1, lv.getChildCount());
                TextView tv = (TextView) lv.getChildAt(NAMES.length).findViewById(R.id.text1);
                assertEquals("Matias", tv.getText());
            }
        });

    }

}
