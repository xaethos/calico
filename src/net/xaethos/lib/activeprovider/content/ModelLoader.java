package net.xaethos.lib.activeprovider.content;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import net.xaethos.lib.activeprovider.models.ActiveModel;

public class ModelLoader<T extends ActiveModel.Base> extends AsyncTaskLoader<ActiveManager.ModelCursor<T>> {

    ////////// Instance variables //////////

    final ForceLoadContentObserver mObserver;
    final Class<T> mModelType;

    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;

    protected ActiveManager.ModelCursor<T> mCursor;

    ////////// Instance methods //////////

    ////// Constructors

    /**
     * Creates a ModelLoader for models of the specified type.
     */
    public ModelLoader(Context context, Class<T> modelType) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mModelType = modelType;
    }

    ///// AsyncTaskLoader

    @Override
    public ActiveManager.ModelCursor<T> loadInBackground() {
        ActiveManager manager = new ActiveManager(getContext());
        ActiveManager.ModelCursor<T> cursor = manager.query(
                mModelType, mProjection, mSelection, mSelectionArgs, mSortOrder);
        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }

    @Override
    public void deliverResult(ActiveManager.ModelCursor<T> cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    ///// Events

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }

    ///// Getters / Setters

    public String[] getProjection() {
        return mProjection;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public String getSelection() {
        return mSelection;
    }

    public void setSelection(String selection) {
        mSelection = selection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }
}
