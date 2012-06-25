package net.xaethos.lib.activeprovider.integration;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import net.xaethos.lib.activeprovider.integration.models.User;
import net.xaethos.lib.activeprovider.models.ModelManager;

public class EditUserActivity extends FragmentActivity {

    private ModelManager mManager;
    private User mUser;

    private EditText mNameField;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        Intent intent = getIntent();
        ModelManager manager = new ModelManager(this);
        User user = null;

        if (Intent.ACTION_INSERT.equals(intent.getAction())) {
            user = manager.create(User.class);
        }
        else if (Intent.ACTION_EDIT.equals(intent.getAction())) {
            user = manager.fetch(User.class, ContentUris.parseId(intent.getData()));
        }

        mManager = manager;
        mUser = user;

        mNameField = (EditText) findViewById(R.id.et_name);
        mNameField.setText(mUser.getName());

        setResult(RESULT_CANCELED);
    }

    ///// Events

    public void onSave(View view) {
        User user = mUser;

        user.setName(mNameField.getText().toString());

        if (mManager.save(user)) {
            Intent intent = new Intent(getIntent());
            intent.setData(user.getUri());
            setResult(RESULT_OK, new Intent());
        }

        finish();
    }
}