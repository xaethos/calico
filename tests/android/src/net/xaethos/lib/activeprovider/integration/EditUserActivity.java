package net.xaethos.lib.activeprovider.integration;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import net.xaethos.lib.activeprovider.content.ActiveManager;
import net.xaethos.lib.activeprovider.integration.models.User;

public class EditUserActivity extends FragmentActivity {

    private ActiveManager mManager;
    private User mUser;

    private EditText etName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        mManager = new ActiveManager(this);

        Intent intent = getIntent();
        mUser = mManager.fetch(User.class, ContentUris.parseId(intent.getData()));

        etName = (EditText) findViewById(R.id.et_name);
        etName.setText(mUser.getName());
    }

    ///// Events

    public void onSave(View view) {
        finish();
    }
}