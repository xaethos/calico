package net.xaethos.lib.activeprovider.integration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import net.xaethos.lib.activeprovider.integration.models.User;
import net.xaethos.lib.activeprovider.models.ModelLoader;
import net.xaethos.lib.activeprovider.models.ModelManager;

public class UsersActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<ModelManager.ModelCursor<User>>,
        AdapterView.OnItemClickListener
{

    private ModelManager.ModelCursor<User> mCursor;
    private SimpleCursorAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView list = new ListView(this);
        setContentView(list);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1, null,
                new String[]{User.NAME},
                new int[]{android.R.id.text1},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mAdapter = adapter;

        list.setId(android.R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        registerForContextMenu(list);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    ///// Options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("New User");
        item.setIntent(new Intent(Intent.ACTION_INSERT, ModelManager.getContentUri(User.class)));
        return true;
    }

    ///// Context menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View itemView, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, itemView, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_user_edit:
                editUser(info.id);
                return true;
            case R.id.menu_user_delete:
                mCursor.moveToPosition(info.position);
                new ModelManager(this).delete(mCursor.getModel());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    ///// Helpers

    private void editUser(long userId) {
        startActivity(new Intent(Intent.ACTION_EDIT, ModelManager.getContentUri(User.class, userId)));
    }

    ////////// LoaderManager.LoaderCallbacks //////////

    @Override
    public Loader<ModelManager.ModelCursor<User>> onCreateLoader(int i, Bundle bundle) {
        return new ModelLoader<User>(this, User.class);
    }

    @Override
    public void onLoadFinished(Loader<ModelManager.ModelCursor<User>> loader, ModelManager.ModelCursor<User> cursor) {
        mCursor = cursor;
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<ModelManager.ModelCursor<User>> loader) {
        mCursor = null;
        mAdapter.swapCursor(null);
    }

    ////////// AdapterView.OnItemClickListener //////////

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        editUser(id);
    }
}