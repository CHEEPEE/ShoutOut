package com.announcement.schol.infoboard.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.adapter.PostFeedRecyclerViewAdapter;
import com.announcement.schol.infoboard.fragment.AdminPostFragment;
import com.announcement.schol.infoboard.model.PostFeedModel;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewFeed extends AppCompatActivity {
    private DatabaseReference mPostDatabaseRef;
    private String getUserType="";
    private RecyclerView postFeedRecyclerView;
    private PostFeedRecyclerViewAdapter postFeedRecyclerViewAdapter;
    private ArrayList<PostFeedModel> postFeedModelsArray = new ArrayList<>();
    FirebaseAuth mAuth,mAuthUser;
    TextView accountName;
    TextView accountEmail;

    CircleImageView accountImage;
    Button btnSignOut;

    private SlidingRootNav slidingRootNav;
    private DatabaseReference databaseRefUsers;
    Toolbar toolbar;
    private String getCurrentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feed);
        toolbar = (Toolbar) findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setElevation(0);
        }
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseRefUsers = FirebaseDatabase.getInstance().getReference();
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_layout)
                .withDragDistance(250)
                .withRootViewScale(.8f)
                .withToolbarMenuToggle(toolbar)
                .withContentClickableWhenMenuOpened(true)
                .inject();
        accountImage = (CircleImageView) findViewById(R.id.account_img);
        accountName = (TextView) findViewById(R.id.text_acount_name);
        accountEmail= (TextView) findViewById(R.id.text_account_email);
        accountName.setText(mAuth.getCurrentUser().getDisplayName().toString());
        accountEmail.setText(mAuth.getCurrentUser().getEmail().toString());
        Picasso.with(NewFeed.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(accountImage);
        loadFragment(new AdminPostFragment());


        //drawer Item Menus
        btnSignOut = (Button) findViewById(R.id.btn_logout);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        databaseRefUsers.child("users").child(getCurrentUserId).child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getUserType = dataSnapshot.getValue().toString();
                System.out.println("User type: "+getUserType);
                if (getUserType.equals("admin")){
                    getMenuInflater().inflate(R.menu.admin_menu_newsfeed,menu);
                }else {
                    getMenuInflater().inflate(R.menu.user_menu_newsfeed,menu);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
               signOut();

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

    private void signOut(){
        LoginManager.getInstance().logOut();
        mAuth.signOut();
        Intent singOutIntent = new Intent(NewFeed.this,LoginActivity.class);
        startActivity(singOutIntent);
    }
}
