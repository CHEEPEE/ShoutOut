package com.announcement.schol.infoboard.postmodule.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class CreatePostMapModel {

    public String author;
    public String title;
    public String textBody;
    public String imgURL;
    public String postImageUrl;
    public String postKey;
    public String timestamp;
    public String postType;
    public String authorID;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public CreatePostMapModel() {

    }

    public CreatePostMapModel(String author, String postTitle, String postTextCotent, String postImg,String uri,String postkey,String postAuthorId,String timeDate) {
        this.author = author;
        this.title = postTitle;
        this.textBody = postTextCotent;
        this.imgURL = postImg;
        this.postImageUrl = uri;
        this.postKey = postkey;
        this.authorID = postAuthorId;
        this.timestamp = timeDate;
    }
    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author",author);
        result.put("authorID",authorID);
        result.put("title",title);
        result.put("textBody", textBody);
        result.put("imgURL", imgURL);
        result.put("postImageUrl",postImageUrl);
        result.put("postKey",postKey);
        result.put("timestamp",timestamp);



        return result;
    }
    // [END post_to_map]

}