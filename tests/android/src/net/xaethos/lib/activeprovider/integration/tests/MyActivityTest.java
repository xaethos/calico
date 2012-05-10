package net.xaethos.lib.activeprovider.integration.tests;

import android.R;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
import net.xaethos.lib.activeprovider.integration.MyActivity;
import net.xaethos.lib.activeprovider.integration.models.User;
import net.xaethos.lib.activeprovider.models.ModelManager;

public class MyActivityTest extends ActivityInstrumentationTestCase2<MyActivity> {

    Uri usersUri;

    public MyActivityTest() {
        super(MyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        usersUri = ModelManager.getContentUri(User.class);
        getInstrumentation().getTargetContext().getContentResolver().delete(usersUri, null, null);
    }

    public void testOnCreate_createsListView() {
        assertNotNull(getActivity().findViewById(R.id.list));
    }

    public void test_cursorLoading() throws Exception {
        final MyActivity activity = getActivity();

        activity.createUser("Pedro");
        activity.createUser("Juan");
        activity.createUser("Diego");

        getInstrumentation().waitForIdle(new Runnable() {
            @Override
            public void run() {
                ListView lv = (ListView) activity.findViewById(R.id.list);
                assertEquals(3, lv.getChildCount());
                for (String name : new String[]{"Pedro", "Juan", "Diego"}) {
                    TextView tv = (TextView) lv.getChildAt(0).findViewById(R.id.text1);
                    assertEquals(name, tv.getText());
                }
            }
        });

    }

}
