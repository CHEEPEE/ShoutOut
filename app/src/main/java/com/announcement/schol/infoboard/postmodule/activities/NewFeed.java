package com.announcement.schol.infoboard.postmodule.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import com.announcement.schol.infoboard.loginmodule.activities.LoginActivity;
import com.announcement.schol.infoboard.postmodule.adapter.PostFeedRecyclerViewAdapter;
import com.announcement.schol.infoboard.postmodule.fragment.AdminPostFragment;
import com.announcement.schol.infoboard.postmodule.fragment.FreedomWallFragement;
import com.announcement.schol.infoboard.postmodule.model.PostFeedModel;
import com.announcement.schol.infoboard.utils.Utilities;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class NewFeed extends AppCompatActivity {
    private DatabaseReference mPostDatabaseRef;
    private String getUserType="";
    private RecyclerView postFeedRecyclerView;
    String[] menuItems = {"announcement","freedonwall"};
    private PostFeedRecyclerViewAdapter postFeedRecyclerViewAdapter;
    private ArrayList<PostFeedModel> postFeedModelsArray = new ArrayList<>();
    FirebaseAuth mAuth,mAuthUser;
    TextView accountName,accountEmail;
    TextView announcement;
    TextView[] textItems = new TextView[menuItems.length];
    static boolean calledAlready = false;
    CircleImageView accountImage;
    Button btnSignOut,btnRemoveAds;
    Menu actionBarMenu;
    String paiduser;


    private SlidingRootNav slidingRootNav;
    private DatabaseReference databaseRefUsers;
    private FirebaseDatabase mFirebaseDatabase;
    Toolbar toolbar;
    private AdView mAdView;
    private String getCurrentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feed);
        toolbar = (Toolbar) findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }


        MobileAds.initialize(NewFeed.this,getString(R.string.admobAppId));

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        System.out.println("Current Time Date"+Utilities.getDateToStrig());
        System.out.println("paiduser"+FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("paiduser"));
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("paiduser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    System.out.println("paiduser:" +dataSnapshot.getValue());
                    if (dataSnapshot.getValue().equals("paid")){
                        mAdView.getLayoutParams().height=0;
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //toolBar Initialize
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setElevation(0);
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        getCurrentUserId = mAuth.getCurrentUser().getUid();

        databaseRefUsers = FirebaseDatabase.getInstance().getReference();
        databaseRefUsers.keepSynced(true);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_layout)
                .withDragDistance(250)
                .withRootViewScale(1f)
                .withToolbarMenuToggle(toolbar)
                .withContentClickableWhenMenuOpened(true)
                .inject();
        textItems[0] = (TextView) findViewById(R.id.announcement);
        textItems[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AdminPostFragment());
                slidingRootNav.closeMenu(true);
                itemSeletct(menuItems[0]);

            }
        });
        textItems[1] = (TextView) findViewById(R.id.freedom_wall);
        textItems[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new FreedomWallFragement());
                slidingRootNav.closeMenu(true);
                itemSeletct(menuItems[1]);
            }
        });

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
        btnRemoveAds = (Button) findViewById(R.id.btn_remove_ads);
        btnRemoveAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        });
        //firebase for OfflineMode Get instance


    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        databaseRefUsers.child("users").child(getCurrentUserId).child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getUserType = dataSnapshot.getValue().toString();
                System.out.println("User type: "+getUserType);
                actionBarMenu = menu;
                if (getUserType.equals("admin")){
                    getMenuInflater().inflate(R.menu.admin_menu_newsfeed,menu);
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
        finish();
    }

    private void itemSeletct(final String item) {
        for (int i = 0; i < menuItems.length; i++) {
           final int index = i;

            databaseRefUsers.child("users").child(getCurrentUserId).child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getUserType = dataSnapshot.getValue().toString();
                    System.out.println("User type: "+getUserType);
                    if (getUserType.equals("admin")){
                        if(item.equals(menuItems[index])){
                            textItems[index].setTextColor(getResources().getColor(R.color.colorAccent));
                            getSupportActionBar().setTitle("Freedom Wall");
                            actionBarMenu.clear();
                            getMenuInflater().inflate(R.menu.blank_menu,actionBarMenu);
                        }else {
                            textItems[index].setTextColor(getResources().getColor(R.color.darkGrey));
                            getSupportActionBar().setTitle("Announcement");
                            actionBarMenu.clear();
                            getMenuInflater().inflate(R.menu.admin_menu_newsfeed,actionBarMenu);
                        }
                    }else {
                        if(item.equals(menuItems[index])){
                            textItems[index].setTextColor(getResources().getColor(R.color.colorAccent));
                            getSupportActionBar().setTitle("Freedom Wall");
                            actionBarMenu.clear();
                            getMenuInflater().inflate(R.menu.blank_menu,actionBarMenu);
                        }else {
                            textItems[index].setTextColor(getResources().getColor(R.color.darkGrey));
                            getSupportActionBar().setTitle("Announcement");
                            actionBarMenu.clear();
                            getMenuInflater().inflate(R.menu.blank_menu,actionBarMenu);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
