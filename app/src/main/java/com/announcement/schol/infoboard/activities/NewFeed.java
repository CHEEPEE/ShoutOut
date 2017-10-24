package com.announcement.schol.infoboard.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.announcement.schol.infoboard.fragment.AdminPostFragment;
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



        accountImage = (CircleImageView) findViewById(R.id.account_img) ;
        accountName = (TextView) findViewById(R.id.text_acount_name);
        accountEmail= (TextView) findViewById(R.id.text_account_email);
        accountName.setText(mAuth.getCurrentUser().getDisplayName().toString());
        accountEmail.setText(mAuth.getCurrentUser().getEmail().toString());
        Picasso.with(NewFeed.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(accountImage);

        loadFragment(new AdminPostFragment());



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
    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.fragement_layout,fragment);
        fragmentTransaction.commit(); // save the changes
    }
}
