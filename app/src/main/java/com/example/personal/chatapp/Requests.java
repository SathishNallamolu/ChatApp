package com.example.personal.chatapp;

/**
 * Created by Personal on 14-11-2018.
 */

public class Requests {
    private String userName,userStatus,thumb_image;

    public Requests(){

    }

    public Requests(String userName, String userStatus, String thumb_image) {
        this.userName = userName;
        this.userStatus = userStatus;
        this.thumb_image = thumb_image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
