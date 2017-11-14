package com.announcement.schol.infoboard.postmodule.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.blurbehind.BlurBehind;
import com.announcement.schol.infoboard.blurbehind.OnBlurCompleteListener;
import com.announcement.schol.infoboard.postmodule.activities.CommentsActivity;
import com.announcement.schol.infoboard.postmodule.activities.PostImageActivity;
import com.announcement.schol.infoboard.postmodule.model.CommentModel;
import com.announcement.schol.infoboard.postmodule.model.PostFeedModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Keji's Lab on 23/10/2017.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<CommentModel> commentModels;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView userAccountImage;
        public TextView UserName,userComment,timeStamp;
        public MyViewHolder(View view){
            super(view);
            userAccountImage = (CircleImageView) view.findViewById(R.id.user_account_image);
            UserName = (TextView) view.findViewById(R.id.user_name);
            userComment = (TextView) view.findViewById(R.id.user_comment);
            timeStamp = (TextView) view.findViewById(R.id.comment_timestamp);

        }
    }

    public CommentRecyclerViewAdapter(Context c, ArrayList<CommentModel> commentModels){
        this.commentModels = commentModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_single,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentRecyclerViewAdapter.MyViewHolder holder, final int position) {
        CommentModel commentModel1 = commentModels.get(position);
        Picasso.with(context).load(commentModel1.getUserAccountImageUrl()).into(holder.userAccountImage);
        holder.UserName.setText(commentModel1.getAuthor());
        holder.userComment.setText(commentModel1.getMsg());
        holder.timeStamp.setText(commentModel1.getTimeStamp());

    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }
}
