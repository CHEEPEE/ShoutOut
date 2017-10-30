package com.announcement.schol.infoboard.postmodule.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.postmodule.adapter.PostFeedRecyclerViewAdapter;
import com.announcement.schol.infoboard.postmodule.model.CreatePostMapModel;
import com.announcement.schol.infoboard.postmodule.model.PostFeedModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Keji's Lab on 24/10/2017.
 */

public class AdminPostFragment extends Fragment {
    private View view;
    private DatabaseReference mPostDatabaseRef;
    private RecyclerView postFeedRecyclerView;
    private PostFeedRecyclerViewAdapter postFeedRecyclerViewAdapter;
    private ArrayList<PostFeedModel> postFeedModelsArray = new ArrayList<>();
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_post_feed, container, false);

        postFeedRecyclerViewAdapter = new PostFeedRecyclerViewAdapter(getActivity(),postFeedModelsArray);
        postFeedRecyclerView = (RecyclerView) view.findViewById(R.id.post_feed);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postFeedRecyclerView.setLayoutManager(layoutManager);
        postFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postFeedRecyclerView.setAdapter(postFeedRecyclerViewAdapter);

        mPostDatabaseRef = FirebaseDatabase.getInstance().getReference().child("post");
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
                    postFeedModelsArray.add(FeedMdel);
                    postFeedRecyclerViewAdapter.notifyDataSetChanged();
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
