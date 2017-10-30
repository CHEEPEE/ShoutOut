package com.announcement.schol.infoboard.postmodule.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.blurbehind.BlurBehind;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class PostImageActivity extends AppCompatActivity {
    PhotoView postImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);
        postImage = (PhotoView) findViewById(R.id.postImage);
        BlurBehind.getInstance()
                .withAlpha(90)
                .withFilterColor(Color.parseColor("#009688"))
                .setBackground(this);
        Bundle bundle = getIntent().getExtras();
        String imgUrl = bundle.getString("imgUrl");
        System.out.println(imgUrl);
        Picasso.with(this).load(imgUrl).into(postImage);
    }
}
