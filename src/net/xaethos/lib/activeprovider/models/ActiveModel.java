package net.xaethos.lib.activeprovider.models;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;

import java.util.Date;

public class ActiveModel {

    /////////////// Inner classes ///////////////

    @SuppressWarnings("UnusedDeclaration")
    public static interface Base<T extends Base> extends Utils<T> {
        public static final String _ID = BaseColumns._ID;

        @Getter(_ID) public Long getId();
    }

    @SuppressWarnings("UnusedDeclaration")
    public static interface Timestamps {
        public static final String _CREATED_AT = "_created_at";
        public static final String _UPDATED_AT = "_updated_at";

        @Getter(_CREATED_AT) public Date getCreatedAt();
        @Getter(_UPDATED_AT) public Date getUpdatedAt();
    }

    public static interface Utils<T extends Base> {
        public boolean isReadOnly();
        public T writableCopy();

        public ContentProviderOperation saveOperation();
    }

    /////////////// Static methods ///////////////

    public static boolean isModelInterface(Class<?> modelType) {
        return modelType.isInterface() &&
                Base.class.isAssignableFrom(modelType) &&
                modelType.isAnnotationPresent(ModelInfo.class);
    }

    public static ModelInfo getModelInfo(Class<?> modelType) {
        if (!isModelInterface(modelType)) {
            throw new IllegalArgumentException(
                    modelType.getName() + "must be an interface with the annotation @" + ModelInfo.class.getSimpleName());
        }
        return modelType.getAnnotation(ModelInfo.class);
    }

    private static Uri.Builder buildContentUri(ModelInfo model) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(model.authority())
                .appendPath(model.tableName());
    }

    public static Uri getContentUri(ModelInfo model) {
        return buildContentUri(model).build();
    }

    public static Uri getContentUri(ModelInfo model, long id) {
        return buildContentUri(model).appendPath(Long.toString(id)).build();
    }

    public static Uri getContentUri(Class<?> modelType) {
        return getContentUri(modelType.getAnnotation(ModelInfo.class));
    }

    public static Uri getContentUri(Class<?> modelType, long id) {
        return getContentUri(modelType.getAnnotation(ModelInfo.class), id);
    }

    public static String getContentDirType(ModelInfo model) {
        return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + model.contentType();
    }

    public static String getContentDirType(Class<?> modelInterface) {
        return getContentDirType(getModelInfo(modelInterface));
    }

    public static String getContentItemType(ModelInfo model) {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + model.contentType();
    }

    public static String getContentItemType(Class<?> modelInterface) {
        return getContentItemType(getModelInfo(modelInterface));
    }

}
