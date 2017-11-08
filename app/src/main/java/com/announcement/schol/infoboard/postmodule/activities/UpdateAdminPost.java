package com.announcement.schol.infoboard.postmodule.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.postmodule.model.CreatePostMapModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UpdateAdminPost extends AppCompatActivity {
    DatabaseReference mDatabase;
    StorageReference mStorageRef;
    String postKey;
    EditText title,content;
    String author,userImg,uid,imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_admin_post);
        String getPostKey = getIntent().getExtras().getString("postkey");
        mDatabase = FirebaseDatabase.getInstance().getReference().child(getIntent().getExtras().getString("postType"));
        mStorageRef = FirebaseStorage.getInstance().getReference();
        postKey = getPostKey;
        title = (EditText) findViewById(R.id.field_title);
        content = (EditText) findViewById(R.id.field_content);
        title.setText(getIntent().getExtras().getString("title"));
        content.setText(getIntent().getExtras().getString("contentBody"));
        author = getIntent().getExtras().getString("author");
        userImg = getIntent().getExtras().getString("userImg");
        uid = getIntent().getExtras().getString("uid");
        imgUrl = getIntent().getExtras().getString("imgUrl");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.createpostmenu, menu);

        // Configure the search info and add any event listeners...
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_publish_post:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (validateForm(title,content)){
                    MaterialDialog dialog =  new MaterialDialog.Builder(UpdateAdminPost.this)
                            .content("Posting")
                            .show();
                    String key = postKey;
                    CreatePostMapModel updatepostModel = new CreatePostMapModel(author,title.getText().toString(),content.getText().toString()
                    ,userImg,imgUrl,key,uid);
                    Map<String, Object> postValue = updatepostModel.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key, postValue);
                    mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            finish();
                        }
                    });
                }
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean validateForm(EditText title, EditText content) {
        boolean valid = true;
        String Title = title.getText().toString();
        if (TextUtils.isEmpty(Title)) {
            title.setError("Required.");
            valid = false;
        } else {
            title.setError(null);
        }

        String Content = content.getText().toString();
        if (TextUtils.isEmpty(Content)) {
            content.setError("Required.");
            valid = false;
        } else {
            content.setError(null);
        }

        return valid;
    }
}
