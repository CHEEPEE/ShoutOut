package com.announcement.schol.infoboard.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.announcement.schol.infoboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewFeed extends AppCompatActivity {

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
