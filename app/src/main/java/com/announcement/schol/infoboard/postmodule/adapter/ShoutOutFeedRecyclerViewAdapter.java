package com.announcement.schol.infoboard.postmodule.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.blurbehind.BlurBehind;
import com.announcement.schol.infoboard.blurbehind.OnBlurCompleteListener;
import com.announcement.schol.infoboard.postmodule.activities.CommentsActivity;
import com.announcement.schol.infoboard.postmodule.activities.PostImageActivity;
import com.announcement.schol.infoboard.postmodule.activities.ShoutOutCommentsActivity;
import com.announcement.schol.infoboard.postmodule.activities.UpdateAdminPost;
import com.announcement.schol.infoboard.postmodule.model.PostFeedModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Keji's Lab on 23/10/2017.
 */

public class ShoutOutFeedRecyclerViewAdapter extends RecyclerView.Adapter<ShoutOutFeedRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<PostFeedModel> postFeedModels;
    private Context context;
    private ArrayList<Boolean> getNull;
    private int Comment_Number=0;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title,author,bodyContent,commentNumber;
        public CircleImageView accountImage;
        public ImageView postImage,postOption;
        public Button btnComment,btnShare;

        public MyViewHolder(View view){
            super(view);
            postImage = (ImageView)view.findViewById(R.id.post_img);

           /* btnComment = (Button) view.findViewById(R.id.btn_comment);
            btnShare= (Button) view.findViewById(R.id.btn_share);*/
            title = (TextView) view.findViewById(R.id.post_title);
            author = (TextView) view.findViewById(R.id.post_author);
            bodyContent = (TextView) view.findViewById(R.id.body_content);
            accountImage = (CircleImageView) view.findViewById(R.id.account_img);
            btnComment = (Button) view.findViewById(R.id.btn_comment);
            postOption = (ImageView) view.findViewById(R.id.post_option);
            commentNumber = (TextView) view.findViewById(R.id.comment_number);
            btnShare = (Button) view.findViewById(R.id.btn_share);
        }
    }

    public ShoutOutFeedRecyclerViewAdapter(Context c, ArrayList<PostFeedModel> postFeedModels){
        this.postFeedModels = postFeedModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_layout_item,parent,false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ShoutOutFeedRecyclerViewAdapter.MyViewHolder holder, final int position) {

        final PostFeedModel postFeedModel = postFeedModels.get(position);



        //FirebaseDatabase mstorageRef = postFeedModel.getPostImageUrl();
       // Picasso.with(context).load(postFeedModel.getPostImageUrl()).centerInside().resize(900,900).into(holder.postImage);
        if (postFeedModel.getPostImageUrl().equals(null) || postFeedModel.getPostImageUrl().equals("null")){
            Glide.clear(holder.postImage);
            holder.postImage.getLayoutParams().height=0;
        }else {
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl(postFeedModel.getPostImageUrl());
            Glide.with(context).using(new FirebaseImageLoader()).load(firebaseStorage).override(600,600).into(holder.postImage);
            holder.postImage.getLayoutParams().height=600;
        }
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(postFeedModel.getmAuthorID())){
            holder.postOption.setVisibility(View.VISIBLE);
            holder.postOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postOptionMenu(position);
                }
            });
        }else {
            Glide.clear(holder.postOption);
            holder.postOption.setVisibility(View.INVISIBLE);
        }

        FirebaseDatabase.getInstance().getReference().child("shoutOutComment").child(postFeedModel.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Comment_Number=0;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Comment_Number++;
                }
                holder.commentNumber.setText(""+Comment_Number);
                System.out.println(""+Comment_Number);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        //soocial Section
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(postFeedModel.getKey());
                Activity activity = (Activity) context;
                BlurBehind.getInstance().execute(activity, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(context, ShoutOutCommentsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("shoutOutcommentKey",postFeedModel.getKey());
                        context.startActivity(intent);
                    }
                });

            }
        });

        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,postFeedModel.getPostTitle()+"\n"+postFeedModel.getContent()
                +"\n\n"+postFeedModel.getPostImageUrl());
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postFeedModels.size();
    }

    private void postOptionMenu(final int position){
        final String[] options = {"Edit","Delete"};
        new MaterialDialog.Builder(context)
                .items(options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (text.toString()){
                            case "Edit":

                                PostFeedModel postFeedModeler = postFeedModels.get(position);

                                Intent i = new Intent(context, UpdateAdminPost.class);
                                i.putExtra("postkey",postFeedModels.get(position).getKey()).putExtra("uid",postFeedModeler.getmAuthorID())
                                        .putExtra("imgUrl",postFeedModeler.getPostImageUrl())
                                        .putExtra("userImg",postFeedModeler.getAuthorImg())
                                        .putExtra("title",postFeedModeler.getPostTitle())
                                        .putExtra("contentBody",postFeedModeler.getContent())
                                        .putExtra("author",postFeedModeler.getAuthor()).putExtra("postType","shoutOutPost");
                                context.startActivity(i);

                                break;
                            case "Delete":
                               MaterialDialog materialDialog =  new MaterialDialog.Builder(context)
                                        .title("Detele")
                                        .content("Are You Sure to Delete this Post. You can't see this post in the future")
                                        .positiveText("Procced").onPositive(new MaterialDialog.SingleButtonCallback() {
                                           @Override
                                           public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                               FirebaseDatabase.getInstance().getReference().child("shoutOutPost").child(postFeedModels.get(position).getKey()).removeValue();
                                               FirebaseDatabase.getInstance().getReference().child("shoutOutComment").child(postFeedModels.get(position).getKey()).removeValue();
                                               if (!postFeedModels.get(position).getPostImageUrl().equals("null")) {
                                                   FirebaseStorage.getInstance().getReferenceFromUrl(postFeedModels.get(position).getPostImageUrl()).delete();
                                               }

                                           }
                                       })
                                        .negativeText("Cancel")
                                        .show();
                                break;
                        }
                    }
                })
                .show();
    }



}
