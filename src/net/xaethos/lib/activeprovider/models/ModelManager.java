package net.xaethos.lib.activeprovider.models;

import android.net.Uri;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;

public class ModelManager {

    /////////////// Constants ///////////////

    private static final String MIME_BASE_DIR  = "vnd.android.cursor.dir/";
    private static final String MIME_BASE_ITEM = "vnd.android.cursor.item/";

    /////////////// Static methods ///////////////

    public static boolean isModelInterface(Class<?> modelType) {
        return modelType.isInterface() &&
                ActiveModel.class.isAssignableFrom(modelType) &&
                modelType.isAnnotationPresent(ModelInfo.class);
    }

    public static ModelInfo getModelInfo(Class<?> modelType) {
        if (!isModelInterface(modelType)) {
            throw new IllegalArgumentException(
                    modelType.getName() + "must be an interface with the annotation @" + ModelInfo.class.getSimpleName());
        }
        return modelType.getAnnotation(ModelInfo.class);
    }

    public static Uri getContentUri(ModelInfo model) {
        return new Uri.Builder()
                .scheme("content")
                .authority(model.authority())
                .appendPath(model.tableName())
                .build();
    }

    public static Uri getContentUri(Class<?> modelType) {
        return getContentUri(modelType.getAnnotation(ModelInfo.class));
    }

    public static String getContentDirType(ModelInfo model) {
        return MIME_BASE_DIR + model.contentType();
    }

    public static String getContentDirType(Class<?> modelInterface) {
        return getContentDirType(getModelInfo(modelInterface));
    }

    public static String getContentItemType(ModelInfo model) {
        return MIME_BASE_ITEM + model.contentType();
    }

    public static String getContentItemType(Class<?> modelInterface) {
        return getContentItemType(getModelInfo(modelInterface));
    }

}
