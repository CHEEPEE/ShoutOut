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
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreatePostActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("post");
        getSupportActionBar().setTitle("What's in your mind?");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
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
                publishPost(mAuth.getCurrentUser().getDisplayName(),title.getText().toString(),mAuth.getCurrentUser().getPhotoUrl().toString(),content.getText().toString(),imageToUploadUri);

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
        Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);

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

    /*public void uploadFileFromInputStream(final Uri uri){
        InputStream file = null;
        try {
            file = getContentResolver().openInputStream(uri);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        System.out.println(getFileName(uri));
        final String type;
        if (getFileName(uri).substring(getFileName(uri).length()-3,getFileName(uri).length()).toLowerCase().equals("jpg") || getFileName(uri).substring(getFileName(uri).length()-3,getFileName(uri).length()).toLowerCase().equals("png")){
            type = "image";
        }else {
            type="file";
        }

        final String msgTitle;

        if (msgField.getText().toString().trim().equals("") || msgField.getText().toString().equals(null)){
            msgTitle = getFileName(uri);
        }else {
            msgTitle = msgField.getText().toString();

        }


        StorageReference riversRef = mStorageRef.child("files/"+ File.separator+getFileName(uri)+file.toString()+File.separator+getFileName(uri));
        final String downloadURL = "files/"+File.separator+getFileName(uri)+file.toString()+File.separator+getFileName(uri);
        riversRef.putStream(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        @SuppressWarnings("VisibleForTests")
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    *//*Uri downloadUrl = taskSnapshot.getDownloadUrl();*//*

                        System.out.println("Upload Success");
                        System.out.println(downloadUri);
                        String name = mAuth.getCurrentUser().getDisplayName();
                        String userId = mAuth.getCurrentUser().getUid();
                        String uEmail = mAuth.getCurrentUser().getEmail();
                        String msg = msgField.getText().toString();
                        String timeStamp = Utils.formatTheDate(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                        writeNewMsg(uEmail,name,msgTitle.toString(),timeStamp,downloadUri,type);
                        messageListAdapter.notifyDataSetChanged();
                        uploadProgress.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        System.out.println("Upload Failed");
                    }
                });
    }*/

    private void publishPost(final String author,final String title ,final String userImgUrl,final String content,Uri uri){
        if (uri!= null){
            InputStream file = null;
            try {
                file = getContentResolver().openInputStream(uri);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            StorageReference imageRef = mStorageRef.child("files/"+ File.separator+getFileName(uri)+file.toString()+File.separator+getFileName(uri));
            imageRef.putStream(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    String imageUrl = taskSnapshot.getDownloadUrl().toString();
                    String key = mDatabase.push().getKey();
                    CreatePostMapModel createPostMapModel = new CreatePostMapModel(author,title,content,userImgUrl,imageUrl);
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

                }
            });


        }else {
            String key = mDatabase.push().getKey();
            CreatePostMapModel createPostMapModel = new CreatePostMapModel(author,title,content,userImgUrl,"null");
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


    }




    }
