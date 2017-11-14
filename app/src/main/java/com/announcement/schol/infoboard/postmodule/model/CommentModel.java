package com.announcement.schol.infoboard.postmodule.model;

/**
 * Created by Keji's Lab on 31/10/2017.
 */

public class CommentModel {
    private String Author;
    private String msg;
    private String userAccountImageUrl;
    private String timeStamp;

    public String getTimeStamp(){
        return timeStamp;
    }
    public String getAuthor(){
        return Author;
    }
    public String getMsg(){
        return msg;
    }
    public String getUserAccountImageUrl(){
        return userAccountImageUrl;
    }
    public void setAuthor(String author){
        this.Author = author;
    }
    public void setMsg(String message){
        this.msg = message;
    }
    public void setUserAccountImageUrl(String userImageUrl){
        this.userAccountImageUrl = userImageUrl;
    }
    public void setTimeStamp(String timeDate){
        this.timeStamp = timeDate;
    }
}
