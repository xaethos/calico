package net.xaethos.lib.activeprovider.models;

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
public class ModelManagerTest {

    private interface NotAnnotated extends ActiveModel {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private abstract class NotAnInterface implements ActiveModel {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private interface NotExtendingModel {}

    ModelInfo dataInfo;

    @Before public void getDataInfo() {
        dataInfo = Data.class.getAnnotation(ModelInfo.class);
    }

    @Test public void isModelInterface() {
        assertThat(ModelManager.isModelInterface(Data.class), is(true));

        assertThat(ModelManager.isModelInterface(NotAnnotated.class), is(false));
        assertThat(ModelManager.isModelInterface(NotAnInterface.class), is(false));
        assertThat(ModelManager.isModelInterface(NotExtendingModel.class), is(false));
    }

    @Test public void getModelInfo() {
        assertThat(ModelManager.getModelInfo(Data.class),
                is(dataInfo));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getModelInfo_shouldThrowException() {
        ModelManager.getModelInfo(NotAnnotated.class);
    }

    @Test public void getContentUri() throws Exception {
        Uri uri = Uri.parse("content://com.example/data");
        assertThat(ModelManager.getContentUri(dataInfo), is(uri));
        assertThat(ModelManager.getContentUri(Data.class), is(uri));
    }

    @Test public void getContentDirType() {
        String mimeType = "vnd.android.cursor.dir/vnd.example.data";
        assertThat(ModelManager.getContentDirType(dataInfo), is(mimeType));
        assertThat(ModelManager.getContentDirType(Data.class), is(mimeType));
    }

    @Test public void getContentItemType() {
        String mimeType = "vnd.android.cursor.item/vnd.example.data";
        assertThat(ModelManager.getContentItemType(dataInfo), is(mimeType));
        assertThat(ModelManager.getContentItemType(Data.class), is(mimeType));
    }

}
