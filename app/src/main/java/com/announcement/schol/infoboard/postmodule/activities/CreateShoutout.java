package com.announcement.schol.infoboard.postmodule.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.postmodule.model.CreatePostMapModel;
import com.announcement.schol.infoboard.utils.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateShoutout extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    FirebaseAuth mAuth;
    FirebaseUser mUsers;
    @BindView(R.id.image_upload)
    ImageView imageToUpload;
    @BindView(R.id.field_title)
    EditText title;
    @BindView(R.id.field_content) EditText content;
    Uri imageToUploadUri;
    Toolbar toolbar;
    AppBarLayout appbarLayout;
    private String getAuthorID;
    @BindView(R.id.clear_image)
    FloatingActionButton floatClearImage;
   private MaterialDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shoutout);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.shoutOutPost));
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shout Out");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));

        mAuth = FirebaseAuth.getInstance();
        getAuthorID = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        floatClearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageToUpload.setImageDrawable(null);
                imageToUploadUri = null;
                floatClearImage.setVisibility(View.GONE);
            }
        });
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
                if (validateForm(title,content)){
                    publishPost(mAuth.getCurrentUser().getDisplayName(),title.getText().toString(),mAuth.getCurrentUser().getPhotoUrl().toString(),content.getText().toString(),imageToUploadUri);
                    dialog =  new MaterialDialog.Builder(CreateShoutout.this)
                            .title("Uploading. . .")
                            .content("Progress")
                            .progress(true, 100)
                            .show();
                }
        }

        return super.onOptionsItemSelected(item);
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
                imageToUploadUri = uri;
            }else {
                System.out.println("null?");
            }

        }

    }

    private void setImage(Uri uri){
        floatClearImage.setVisibility(View.VISIBLE);
        Picasso.with(CreateShoutout.this).load(uri).resize(300,600).into(imageToUpload);

    }


    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void publishPost(final String author,final String title ,final String userImgUrl,final String content,Uri uri){
        if (uri!= null){
            InputStream file = null;
            try {
                file = getContentResolver().openInputStream(uri);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            StorageReference imageRef = mStorageRef.child("imaget/"+getString(R.string.shoutOutPost)+ File.separator+getFileName(uri)+file.toString()+File.separator+getFileName(uri));
            imageRef.putStream(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    String imageUrl = taskSnapshot.getDownloadUrl().toString();
                    String key = mDatabase.push().getKey();
                    CreatePostMapModel createPostMapModel = new CreatePostMapModel(author,title,content,userImgUrl,imageUrl,key,getAuthorID, Utilities.getDateToStrig());
                    Map<String,Object> postValue = createPostMapModel.toMap();
                    Map<String,Object> childUpdates = new HashMap<>();
                    childUpdates.put(key,postValue);
                    mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(CreateShoutout.this,"Error Posting",Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
/*
                    dialog =  new MaterialDialog.Builder(CreateShoutout.this)
                            .content("Progress")
                            .progress(true, 100)
                            .show();
*/



                }
            });
        }else {
            String key = mDatabase.push().getKey();
            CreatePostMapModel createPostMapModel = new CreatePostMapModel(author,title,content,userImgUrl,"null",key,getAuthorID,Utilities.getDateToStrig());
            Map<String,Object> postValue = createPostMapModel.toMap();
            Map<String,Object> childUpdates = new HashMap<>();
            childUpdates.put(key,postValue);
            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(CreateShoutout.this,"Error Posting",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private boolean validateForm(EditText title,EditText content) {
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
