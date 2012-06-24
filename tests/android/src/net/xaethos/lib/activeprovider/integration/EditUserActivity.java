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

        mManager = new ModelManager(this);

        Intent intent = getIntent();
        mUser = mManager.fetch(User.class, ContentUris.parseId(intent.getData()));

        mNameField = (EditText) findViewById(R.id.et_name);
        mNameField.setText(mUser.getName());
    }

    ///// Events

    public void onSave(View view) {
        mUser.setName(mNameField.getText().toString());
        mManager.save(mUser);
        finish();
    }
}