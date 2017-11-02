package com.announcement.schol.infoboard.postmodule.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.postmodule.activities.CreateShoutout;
import com.announcement.schol.infoboard.postmodule.adapter.PostFeedRecyclerViewAdapter;
import com.announcement.schol.infoboard.postmodule.adapter.ShoutOutFeedRecyclerViewAdapter;
import com.announcement.schol.infoboard.postmodule.model.CreatePostMapModel;
import com.announcement.schol.infoboard.postmodule.model.PostFeedModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Keji's Lab on 31/10/2017.
 */

public class FreedomWallFragement extends Fragment {
    private View view;
    private DatabaseReference mPostDatabaseRef;
    private RecyclerView postFeedRecyclerView;
    private ShoutOutFeedRecyclerViewAdapter shoutOutFeedRecyclerViewAdapter;
    private ArrayList<PostFeedModel> postFeedModelsArray = new ArrayList<>();

    private FloatingActionButton shoutOut;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fregment_freedom_wall,container,false);
        shoutOut = (FloatingActionButton) view.findViewById(R.id.create_shoutout);
        shoutOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CreateShoutout.class);
                startActivity(i);
            }
        });
        shoutOutFeedRecyclerViewAdapter = new ShoutOutFeedRecyclerViewAdapter(getActivity(),postFeedModelsArray);
        postFeedRecyclerView = (RecyclerView) view.findViewById(R.id.shout_out_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postFeedRecyclerView.setLayoutManager(layoutManager);
        postFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postFeedRecyclerView.setAdapter(shoutOutFeedRecyclerViewAdapter);

        mPostDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.shoutOutPost));
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
                    FeedMdel.setPostKey(createPostMapModel.postKey);
                    FeedMdel.setAuthorID(createPostMapModel.authorID);
                    postFeedModelsArray.add(FeedMdel);
                    shoutOutFeedRecyclerViewAdapter.notifyDataSetChanged();
                    System.out.println(createPostMapModel.postKey);
                }
                Collections.reverse(postFeedModelsArray);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postFeedRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                return false;
        }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });



        return view;
    }

}
