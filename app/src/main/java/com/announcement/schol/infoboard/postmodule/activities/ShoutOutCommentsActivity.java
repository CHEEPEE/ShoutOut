package com.announcement.schol.infoboard.postmodule.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.postmodule.adapter.CommentRecyclerViewAdapter;
import com.announcement.schol.infoboard.postmodule.model.CommentModel;
import com.announcement.schol.infoboard.postmodule.model.PostCommentModel;
import com.announcement.schol.infoboard.utils.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShoutOutCommentsActivity extends AppCompatActivity {
    CircleImageView accountImage;
    EditText commentField;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabaseRef;
    ImageButton sendComment;
    RecyclerView commentsRecyclerView;
    CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    ArrayList<CommentModel> commentModelsArray = new ArrayList<>();
    Context context;
    public int commentNumber = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        context = ShoutOutCommentsActivity.this;

        final String postcommentKey = getIntent().getExtras().getString("shoutOutcommentKey");
        accountImage = (CircleImageView) findViewById(R.id.account_img);
        firebaseAuth = FirebaseAuth.getInstance();
        commentField = (EditText) findViewById(R.id.comment_field);
        sendComment = (ImageButton) findViewById(R.id.send_comment);
        commentsRecyclerView = (RecyclerView) findViewById(R.id.comment_list);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("shoutOutComment").child(postcommentKey);
        Uri accountImageUrl = firebaseAuth.getCurrentUser().getPhotoUrl();
        Picasso.with(this).load(accountImageUrl).resize(100,100).into(accountImage);
        /*BlurBehind.getInstance()
                .withAlpha(30)
                .withFilterColor(Color.parseColor("#009688"))
                .setBackground(this);*/
        System.out.println(postcommentKey);

        //Send Comment
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mDatabaseRef.push().getKey();
                PostCommentModel postCommentModel = new PostCommentModel(firebaseAuth.getCurrentUser().getDisplayName(),commentField.getText().toString(),firebaseAuth.getCurrentUser().getPhotoUrl().toString(), Utilities.getDateToStrig());
                Map<String,Object> commentValue = postCommentModel.toMap();
                Map<String,Object> childUpdate = new HashMap<>();
                childUpdate.put(key,commentValue);
                sendComment.setEnabled(false);
                mDatabaseRef.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        commentField.setText("");
                        sendComment.setEnabled(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendComment.setEnabled(true);
                        Toast.makeText(ShoutOutCommentsActivity.this,"Comment Failed Check your Network",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        //Comments
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(ShoutOutCommentsActivity.this,commentModelsArray);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ShoutOutCommentsActivity.this);
        commentsRecyclerView.setLayoutManager(layoutManager);
        commentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        commentsRecyclerView.setAdapter(commentRecyclerViewAdapter);

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CommentModel commentModel = new CommentModel();
                PostCommentModel postCommentModel =  dataSnapshot.getValue(PostCommentModel.class);
                commentModel.setAuthor(postCommentModel.author);
                commentModel.setMsg(postCommentModel.message);
                commentModel.setUserAccountImageUrl(postCommentModel.userAccountImageUrl);
                commentModel.setTimeStamp(postCommentModel.timeStamp);
                commentModelsArray.add(commentModel);
                commentRecyclerViewAdapter.notifyDataSetChanged();
                commentNumber++;
                commentsRecyclerView.smoothScrollToPosition(commentNumber);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
