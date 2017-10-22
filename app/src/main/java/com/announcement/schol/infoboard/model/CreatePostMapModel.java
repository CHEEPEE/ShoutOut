package com.announcement.schol.infoboard.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class CreatePostMapModel {

    public String author;
    public String title;
    public String bodyContent;
    public String img;
    public String timestamp;
    public String postType;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public CreatePostMapModel() {

    }

    public CreatePostMapModel(String author, String postTitle, String postContentBody, String postImg) {
        this.author = author;
        this.title = postTitle;
        this.bodyContent = postContentBody;
        this.img = postImg;
    }
    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author",author);
        result.put("title",title);
        result.put("bodycontent", bodyContent);
        result.put("imgURL",img);



        return result;
    }
    // [END post_to_map]

}