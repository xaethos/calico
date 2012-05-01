package net.xaethos.lib.activeprovider.models;

import android.net.Uri;
import net.xaethos.lib.activeprovider.annotations.Model;

public class ModelManager {

    /////////////// Constants ///////////////

    private static final String MIME_BASE_DIR  = "vnd.android.cursor.dir/";
    private static final String MIME_BASE_ITEM = "vnd.android.cursor.item/";

    /////////////// Static methods ///////////////

    public static boolean isModelInterface(Class<?> modelType) {
        return modelType.isInterface() && modelType.isAnnotationPresent(Model.class);
    }

    public static Model getModelInfo(Class<?> modelType) {
        if (!isModelInterface(modelType)) {
            throw new IllegalArgumentException(
                    modelType.getName() + "must be an interface with the annotation @" + Model.class.getSimpleName());
        }
        return modelType.getAnnotation(Model.class);
    }

    public static Uri getContentUri(Model model) {
        return new Uri.Builder()
                .scheme("content")
                .authority(model.authority())
                .appendPath(model.tableName())
                .build();
    }

    public static Uri getContentUri(Class<?> modelType) {
        return getContentUri(modelType.getAnnotation(Model.class));
    }

    public static String getContentDirType(Model model) {
        return MIME_BASE_DIR + model.contentType();
    }

    public static String getContentDirType(Class<?> modelInterface) {
        return getContentDirType(getModelInfo(modelInterface));
    }

    public static String getContentItemType(Model model) {
        return MIME_BASE_ITEM + model.contentType();
    }

    public static String getContentItemType(Class<?> modelInterface) {
        return getContentItemType(getModelInfo(modelInterface));
    }

}
