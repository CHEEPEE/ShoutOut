package com.announcement.schol.infoboard.model;

/**
 * Created by Keji's Lab on 23/10/2017.
 */

public class PostFeedModel {

private String mAuthor,mTitle,mcontent,mAuthorImg,mPostImageUrl;

    public String getAuthor(){
        return mAuthor;
    }
    public String getPostImageUrl(){
        return mPostImageUrl;
    }
    public String getPostTitle(){
        return mTitle;
    }
    public String getContent(){
        return mcontent;
    }
    public String getAuthorImg(){
        return mAuthorImg;
    }
    public void setAuthor(String author){
        this.mAuthor = author;
    }
    public void setTitle(String title){
        this.mTitle = title;
    }
    public void setContent(String content){
        this.mcontent = content;
    }

    public void setAuthorImg(String authorImg){
        this.mAuthorImg = authorImg;
    }
    public void setPostImageURL(String postImageURL){
        this.mPostImageUrl = postImageURL;
    }

}
