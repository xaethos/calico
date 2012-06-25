package net.xaethos.lib.activeprovider.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import com.example.fixtures.Data;
import com.example.fixtures.DataProvider;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContentResolver;
import com.xtremelabs.robolectric.shadows.ShadowSQLiteCursor;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class CalicoProviderTest {

	DataProvider provider;

	ContentResolver resolver;
	ShadowContentResolver resolverShadow;

    ModelInfo dataInfo;
    Uri dirUri;
    Uri itemUri;

    /////////////// Set up ///////////////

	@Before public void instantiateProvider() {
		provider = new DataProvider();
        provider.onCreate();
	}

    @Before public void getModelInfo() {
        dataInfo = Data.class.getAnnotation(ModelInfo.class);

        String authority = dataInfo.authority();
        String tableName = dataInfo.tableName();

        dirUri = Uri.parse("content://" + authority + "/" + tableName);
        itemUri = Uri.parse("content://" + authority + "/" + tableName + "/1");
    }

	@Before public void getResolver() {
		resolver = Robolectric.application.getContentResolver();
		resolverShadow = shadowOf(resolver);
	}

	/////////////// Tests ///////////////

	@Test public void querySetsNotificationUri() {
		SQLiteCursor cursor =
				(SQLiteCursor) provider.query(dirUri, null, null, null, null);
		ShadowSQLiteCursor shadow = shadowOf(cursor);
		assertThat(shadow.getNotificationUri_Compatibility(), is(dirUri));
	}

	@Test public void insertNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");

		resolverShadow.getNotifiedUris().clear();
		provider.insert(dirUri, values);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();
		assertThat(notifiedUris.size(), is(1));
		assertThat(notifiedUris.get(0).uri, is(dirUri));
	}

	@Test public void updateNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");
		Uri uri = provider.insert(dirUri, values);

		resolverShadow.getNotifiedUris().clear();
		values.put(Data.STRING, "Goodbye");
		provider.update(uri, values, null, null);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();

        assertThat(notifiedUris.size(), is(1));
        assertThat(notifiedUris.get(0).uri, is(uri));
    }

	@Test public void deleteNotifiesUri() {
		ContentValues values = new ContentValues();
		values.put(Data.STRING, "Hello");
		Uri uri = provider.insert(dirUri, values);

		resolverShadow.getNotifiedUris().clear();
		provider.delete(uri, null, null);
        List<ShadowContentResolver.NotifiedUri> notifiedUris = resolverShadow.getNotifiedUris();

        assertThat(notifiedUris.size(), is(1));
        assertThat(notifiedUris.get(0).uri, is(uri));
    }

}
