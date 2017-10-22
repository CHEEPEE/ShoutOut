package com.announcement.schol.infoboard.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.model.CreatePostMapModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreatePostActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUsers;
    @BindView(R.id.image_upload)
    ImageView imageToUpload;
    @BindView(R.id.field_title)
    EditText title;
    @BindView(R.id.field_content) EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("post");
        getSupportActionBar().setTitle("What's in your mind?");
        mAuth = FirebaseAuth.getInstance();
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
            case R.id.menu_upload_image:
                performFileSearch();
                break;
            case R.id.menu_publish_post:
                Toast.makeText(CreatePostActivity.this,mAuth.getCurrentUser().getPhotoUrl().toString(),Toast.LENGTH_SHORT).show();
                System.out.println(mAuth.getCurrentUser().getPhotoUrl().toString());
                publishPost(mAuth.getCurrentUser().getDisplayName(),title.getText().toString(),mAuth.getCurrentUser().getPhotoUrl().toString(),content.getText().toString());

        }

        return super.onOptionsItemSelected(item);
    }

    private void publishPost(String author,String title ,String userImgUrl,String content){
        String key = mDatabase.push().getKey();
        CreatePostMapModel createPostMapModel = new CreatePostMapModel(author,title,content,userImgUrl);
        Map<String,Object> postValue = createPostMapModel.toMap();
        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put(key,postValue);
        mDatabase.updateChildren(childUpdates);


    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        //intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");


        startActivityForResult(Intent.createChooser(intent,"Choose File"), READ_REQUEST_CODE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("TAG", "Uri: " + uri.getLastPathSegment());
                setImage(uri);
            }else {
                System.out.println("null?");
            }

        }

    }

    private void setImage(Uri uri){
        Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);
    }






    }
