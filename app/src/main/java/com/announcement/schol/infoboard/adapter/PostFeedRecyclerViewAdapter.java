package com.announcement.schol.infoboard.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.activities.NewFeed;
import com.announcement.schol.infoboard.activities.PostImageActivity;
import com.announcement.schol.infoboard.blurbehind.BlurBehind;
import com.announcement.schol.infoboard.blurbehind.OnBlurCompleteListener;
import com.announcement.schol.infoboard.model.PostFeedModel;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Keji's Lab on 23/10/2017.
 */

public class PostFeedRecyclerViewAdapter extends RecyclerView.Adapter<PostFeedRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<PostFeedModel> postFeedModels;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title,author,bodyContent;
        public CircleImageView accountImage;
        public ImageView postImage;
        public Button btnLike,btnComment,btnShare;
        public MyViewHolder(View view){
            super(view);
            postImage = (ImageView)view.findViewById(R.id.post_img);
            btnLike = (Button) view.findViewById(R.id.btn_like);
            btnComment = (Button) view.findViewById(R.id.btn_comment);
            btnShare= (Button) view.findViewById(R.id.btn_share);
            title = (TextView) view.findViewById(R.id.post_title);
            author = (TextView) view.findViewById(R.id.post_author);
            bodyContent = (TextView) view.findViewById(R.id.body_content);
            accountImage = (CircleImageView) view.findViewById(R.id.account_img);

        }
    }

    public PostFeedRecyclerViewAdapter(Context c,ArrayList<PostFeedModel> postFeedModels){

        this.postFeedModels = postFeedModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_layout_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostFeedRecyclerViewAdapter.MyViewHolder holder, final int position) {

        final PostFeedModel postFeedModel = postFeedModels.get(position);
        Picasso.with(context).load(postFeedModel.getPostImageUrl()).centerInside().resize(700,900).into(holder.postImage);
        holder.title.setText(postFeedModel.getPostTitle());
        holder.author.setText(postFeedModel.getAuthor());
        holder.bodyContent.setText(postFeedModel.getContent());
        Picasso.with(context).load(postFeedModel.getAuthorImg()).resize(200,200).error(R.drawable.ic_account_circle_black_24dp).into(holder.accountImage);
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("image Pos: "+position);

                Activity activity = (Activity) context;
                BlurBehind.getInstance().execute(activity, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {

                        Intent intent = new Intent(context, PostImageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("imgUrl",postFeedModel.getPostImageUrl());
                        System.out.println("postUrlImg"+postFeedModel.getPostImageUrl());
                        context.startActivity(intent);
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return postFeedModels.size();
    }
}
