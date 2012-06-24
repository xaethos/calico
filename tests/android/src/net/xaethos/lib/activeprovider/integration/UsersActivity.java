package net.xaethos.lib.activeprovider.integration;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import net.xaethos.lib.activeprovider.models.ModelManager;
import net.xaethos.lib.activeprovider.models.ModelLoader;
import net.xaethos.lib.activeprovider.integration.models.User;
import net.xaethos.lib.activeprovider.models.ActiveModel;

public class UsersActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<ModelManager.ModelCursor<User>>,
        AdapterView.OnItemClickListener
{

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
        list.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    ////////// LoaderManager.LoaderCallbacks //////////

    @Override
    public Loader<ModelManager.ModelCursor<User>> onCreateLoader(int i, Bundle bundle) {
        return new ModelLoader<User>(this, User.class);
    }

    @Override
    public void onLoadFinished(Loader<ModelManager.ModelCursor<User>> loader, ModelManager.ModelCursor<User> cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<ModelManager.ModelCursor<User>> loader) {
        mAdapter.swapCursor(null);
    }

    ////////// AdapterView.OnItemClickListener //////////

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startActivity(new Intent(Intent.ACTION_EDIT, ActiveModel.getContentUri(User.class, id)));
    }
}