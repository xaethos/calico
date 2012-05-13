package net.xaethos.lib.activeprovider.models;

import android.content.ContentUris;
import android.net.Uri;
import com.example.fixtures.Data;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ActiveModelTest {

    private interface NotAnnotated extends ActiveModel.Base {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private abstract class NotAnInterface implements ActiveModel.Base {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private interface NotExtendingModel {}

    ModelInfo dataInfo;

    @Before public void getDataInfo() {
        dataInfo = Data.class.getAnnotation(ModelInfo.class);
    }

    @Test public void isModelInterface() {
        assertThat(ActiveModel.isModelInterface(Data.class), is(true));

        assertThat(ActiveModel.isModelInterface(NotAnnotated.class), is(false));
        assertThat(ActiveModel.isModelInterface(NotAnInterface.class), is(false));
        assertThat(ActiveModel.isModelInterface(NotExtendingModel.class), is(false));
    }

    @Test public void getModelInfo() {
        assertThat(ActiveModel.getModelInfo(Data.class),
                is(dataInfo));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getModelInfo_shouldThrowException() {
        ActiveModel.getModelInfo(NotAnnotated.class);
    }

    @Test public void getContentUri() throws Exception {
        Uri uri = Uri.parse("content://com.example/data");
        assertThat(ActiveModel.getContentUri(dataInfo), is(uri));
        assertThat(ActiveModel.getContentUri(Data.class), is(uri));

        uri = ContentUris.withAppendedId(uri, 42L);
        assertThat(ActiveModel.getContentUri(dataInfo, 42L), is(uri));
        assertThat(ActiveModel.getContentUri(Data.class, 42L), is(uri));
    }

    @Test public void getContentDirType() {
        String mimeType = "vnd.android.cursor.dir/vnd.example.data";
        assertThat(ActiveModel.getContentDirType(dataInfo), is(mimeType));
        assertThat(ActiveModel.getContentDirType(Data.class), is(mimeType));
    }

    @Test public void getContentItemType() {
        String mimeType = "vnd.android.cursor.item/vnd.example.data";
        assertThat(ActiveModel.getContentItemType(dataInfo), is(mimeType));
        assertThat(ActiveModel.getContentItemType(Data.class), is(mimeType));
    }

}
