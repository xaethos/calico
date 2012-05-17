package net.xaethos.lib.activeprovider.integration;

import android.R;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import net.xaethos.lib.activeprovider.integration.models.User;
import net.xaethos.lib.activeprovider.models.ActiveModel;

public class MyActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView list = new ListView(this);
        setContentView(list);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.simple_list_item_1, null,
                new String[]{User.NAME},
                new int[]{R.id.text1},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mAdapter = adapter;

        list.setId(R.id.list);
        list.setAdapter(adapter);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void createUser(String name) {
        ContentValues values = new ContentValues(1);
        values.put(User.NAME, name);
        getContentResolver().insert(ActiveModel.getContentUri(User.class), values);
    }

    ////////// LoaderManager.LoaderCallbacks //////////

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ActiveModel.getContentUri(User.class), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}