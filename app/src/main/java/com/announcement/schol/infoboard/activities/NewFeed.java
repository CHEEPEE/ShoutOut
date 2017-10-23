package com.announcement.schol.infoboard.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.adapter.PostFeedRecyclerViewAdapter;
import com.announcement.schol.infoboard.model.CreatePostMapModel;
import com.announcement.schol.infoboard.model.PostFeedModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewFeed extends AppCompatActivity {
    private DatabaseReference mPostDatabaseRef;

    private RecyclerView postFeedRecyclerView;
    private PostFeedRecyclerViewAdapter postFeedRecyclerViewAdapter;
    private ArrayList<PostFeedModel> postFeedModelsArray = new ArrayList<>();
    FirebaseAuth mAuth;
    TextView accountName;
     TextView accountEmail;
    CircleImageView accountImage;
    private SlidingRootNav slidingRootNav;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feed);
        mAuth = FirebaseAuth.getInstance();
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_layout)
                .withDragDistance(250)
                .withRootViewScale(1f)
                .inject();


        mPostDatabaseRef = FirebaseDatabase.getInstance().getReference().child("post");
        accountImage = (CircleImageView) findViewById(R.id.account_img) ;
        accountName = (TextView) findViewById(R.id.text_acount_name);
        accountEmail= (TextView) findViewById(R.id.text_account_email);
        accountName.setText(mAuth.getCurrentUser().getDisplayName().toString());
        accountEmail.setText(mAuth.getCurrentUser().getEmail().toString());
        Picasso.with(NewFeed.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(accountImage);

        postFeedRecyclerViewAdapter = new PostFeedRecyclerViewAdapter(NewFeed.this,postFeedModelsArray);
        postFeedRecyclerView = (RecyclerView) findViewById(R.id.post_feed);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        postFeedRecyclerView.setLayoutManager(layoutManager);
        postFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postFeedRecyclerView.setAdapter(postFeedRecyclerViewAdapter);


        mPostDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               /* PostFeedModel FeedMdel = new PostFeedModel();
                CreatePostMapModel createPostMapModel = dataSnapshot.getValue(CreatePostMapModel.class);
                FeedMdel.setAuthor(createPostMapModel.author);
                FeedMdel.setContent(createPostMapModel.textBody);
                FeedMdel.setAuthorImg(createPostMapModel.imgURL);
                FeedMdel.setTitle(createPostMapModel.title);
                System.out.println(createPostMapModel.textBody);
                System.out.println(createPostMapModel.imgURL);
                postFeedModelsArray.add(FeedMdel);
                postFeedRecyclerViewAdapter.notifyDataSetChanged();*/




            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mPostDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postFeedModelsArray.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    PostFeedModel FeedMdel = new PostFeedModel();
                    CreatePostMapModel createPostMapModel = dataSnapshot1.getValue(CreatePostMapModel.class);
                    FeedMdel.setAuthor(createPostMapModel.author);

                    FeedMdel.setContent(createPostMapModel.textBody);
                    FeedMdel.setAuthorImg(createPostMapModel.imgURL);
                    FeedMdel.setPostImageURL(createPostMapModel.postImageUrl);
                    FeedMdel.setTitle(createPostMapModel.title);
                    System.out.println(createPostMapModel.textBody);
                    System.out.println(createPostMapModel.imgURL);
                    postFeedModelsArray.add(FeedMdel);
                    postFeedRecyclerViewAdapter.notifyDataSetChanged();
                }
                Collections.reverse(postFeedModelsArray);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPostDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newsfeed, menu);
        // Configure the search info and add any event listeners...
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_create_post:
                Intent i = new Intent(NewFeed.this,CreatePostActivity.class);
                startActivity(i);
                break;

            case R.id.sign_out_id:
                mAuth.signOut();
                Intent singOutIntent = new Intent(NewFeed.this,LoginActivity.class);
                startActivity(singOutIntent);

        }

        return super.onOptionsItemSelected(item);
    }
}
