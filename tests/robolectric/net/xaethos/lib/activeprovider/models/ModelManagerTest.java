package net.xaethos.lib.activeprovider.models;

import android.net.Uri;
import com.example.fixtures.Data;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ModelManagerTest {

    private interface NotAnnotated extends Model {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private abstract class NotAnInterface implements Model {}

    @ModelInfo(authority = "", contentType = "", tableName = "")
    private interface NotExtendingModel {}


    @Test
    public void testIsModelInterface() {
        assertThat(ModelManager.isModelInterface(Data.class), is(true));

        assertThat(ModelManager.isModelInterface(NotAnnotated.class), is(false));
        assertThat(ModelManager.isModelInterface(NotAnInterface.class), is(false));
        assertThat(ModelManager.isModelInterface(NotExtendingModel.class), is(false));
    }

    @Test
    public void testGetModelInfo() {
        assertThat(ModelManager.getModelInfo(Data.class),
                is(Data.class.getAnnotation(ModelInfo.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getModelInfoShouldThrowException() {
        ModelManager.getModelInfo(NotAnnotated.class);
    }

    @Test
    public void testGetContentUri() throws Exception {
        Uri uri = Uri.parse("content://com.example/data");
        assertThat(ModelManager.getContentUri(Data.class.getAnnotation(ModelInfo.class)), is(uri));
        assertThat(ModelManager.getContentUri(Data.class), is(uri));
    }

    @Test
    public void testGetContentDirType() {
        String mimeType = "vnd.android.cursor.dir/vnd.example.data";
        assertThat(ModelManager.getContentDirType(Data.class.getAnnotation(ModelInfo.class)), is(mimeType));
        assertThat(ModelManager.getContentDirType(Data.class), is(mimeType));
    }

    @Test
    public void testGetContentItemType() {
        String mimeType = "vnd.android.cursor.item/vnd.example.data";
        assertThat(ModelManager.getContentItemType(Data.class.getAnnotation(ModelInfo.class)), is(mimeType));
        assertThat(ModelManager.getContentItemType(Data.class), is(mimeType));
    }

}
