package net.xaethos.lib.calico.models;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import net.xaethos.lib.calico.annotations.Getter;
import net.xaethos.lib.calico.annotations.ModelInfo;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Date;

public class ModelManager {
    private static final String TAG = "ModelManager";

    /////////////// Static methods ///////////////

    public static <T extends CalicoModel> ModelInfo getModelInfo(Class<T> modelType) {
        if (!isModelInterface(modelType)) {
            throw new IllegalArgumentException(
                    modelType.getName() + "must be an interface with the annotation @" + ModelInfo.class.getSimpleName());
        }
        return modelType.getAnnotation(ModelInfo.class);
    }

    public static Uri getContentUri(ModelInfo model) {
        return buildContentUri(model).build();
    }

    public static <T extends CalicoModel> Uri getContentUri(Class<T> modelType) {
        return getContentUri(modelType.getAnnotation(ModelInfo.class));
    }

    public static Uri getContentUri(ModelInfo model, long id) {
        return buildContentUri(model).appendPath(Long.toString(id)).build();
    }

    public static <T extends CalicoModel> Uri getContentUri(Class<T> modelType, long id) {
        return getContentUri(modelType.getAnnotation(ModelInfo.class), id);
    }

    public static String getContentDirType(ModelInfo model) {
        return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + model.contentType();
    }

    public static <T extends CalicoModel> String getContentDirType(Class<T> modelType) {
        return getContentDirType(getModelInfo(modelType));
    }

    public static String getContentItemType(ModelInfo model) {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + model.contentType();
    }

    public static <T extends CalicoModel> String getContentItemType(Class<T> modelType) {
        return getContentItemType(getModelInfo(modelType));
    }

    ///// Helpers

    static <T extends CalicoModel> boolean isModelInterface(Class<T> modelType) {
        return modelType.isInterface() && modelType.isAnnotationPresent(ModelInfo.class);
    }

    static <T extends CalicoModel> T getModel(Class<T> modelClass, ModelHandler<T> handler) {
        return modelClass.cast(Proxy.newProxyInstance(modelClass.getClassLoader(),
                new Class[]{modelClass},
                handler));
    }

    private static Uri.Builder buildContentUri(ModelInfo model) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(model.authority())
                .appendPath(model.tableName());
    }

    /////////////// Instance fields ///////////////

    private final ContentResolver mResolver;

    /////////////// Instance methods ///////////////

    public ModelManager(Context context) {
        mResolver = context.getContentResolver();
    }

    /**
     * Queries the ContentProvider for the table representing a given model.
     * See {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.
     * @return a ModelCursor with the query results
     */
    public <T extends CalicoModel> ModelCursor<T> query(Class<T> modelClass, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return query(modelClass, getContentUri(modelClass), projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Queries the ContentProvider for the table representing a given model.
     * See {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.
     * @return a ModelCursor with the query results
     */
    public <T extends CalicoModel> ModelCursor<T> query(Class<T> modelClass, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ContentProviderClient client = mResolver.acquireContentProviderClient(uri);
        try {
            Cursor cursor = client.query(uri, projection, selection, selectionArgs, sortOrder);
            if (cursor == null) {
                client.release();
                return null;
            }
            return new ModelCursor<T>(cursor, client, modelClass);
        } catch (RemoteException e) {
            client.release();
            return null;
        } catch (RuntimeException e) {
            client.release();
            throw e;
        }
    }

    public <T extends CalicoModel> T create(Class<T> modelType) {
        return getModel(modelType, new ValuesModelHandler<T>(modelType));
    }

    public <T extends CalicoModel> T fetch(Class<T> modelClass, long id) {
        T model = null;
        ModelCursor<T> cursor = query(modelClass, getContentUri(modelClass, id), null, null, null, null);
        if (cursor.moveToFirst()) {
            model = getModel(modelClass, new ValuesModelHandler<T>(modelClass, cursor));
            cursor.close();
        }
        return model;
    }

    public <T extends CalicoModel> boolean save(T model) {
        return applyOperation(model.saveOperation());
    }

    public <T extends CalicoModel> boolean delete(T model) {
        return applyOperation(model.deleteOperation());
    }

    private boolean applyOperation(ContentProviderOperation operation) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(1);
        operations.add(operation);

        try {
            mResolver.applyBatch(operation.getUri().getAuthority(), operations);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Error invoking binder", e);
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error applying operation", e);
        }

        return false;
    }

    /////////////// Inner classes ///////////////

    @SuppressWarnings("UnusedDeclaration")
    public static interface Timestamps {
        public static final String _CREATED_AT = "_created_at";
        public static final String _UPDATED_AT = "_updated_at";

        @Getter(_CREATED_AT) public Date getCreatedAt();
        @Getter(_UPDATED_AT) public Date getUpdatedAt();
    }

    public static interface Utils<T extends CalicoModel> {
        public Uri getUri();

        public boolean isReadOnly();
        public T writableCopy();

        public ContentProviderOperation saveOperation();
        public ContentProviderOperation deleteOperation();
    }

}
