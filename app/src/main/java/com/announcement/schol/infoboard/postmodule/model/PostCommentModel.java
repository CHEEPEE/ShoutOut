package com.announcement.schol.infoboard.postmodule.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class PostCommentModel {

    public String author;
    public String message;
    public String userAccountImageUrl;

    public PostCommentModel() {

    }

    public PostCommentModel(String author, String msg, String userAccountUrl) {
        this.author= author;
        this.message = msg;
        this.userAccountImageUrl = userAccountUrl;

    }
    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author",author);
        result.put("message", message);
        result.put("userAccountImageUrl",userAccountImageUrl);




        return result;
    }
}