package com.example.personal.chatapp;


public class UserProfile {
    public String userName, userEmail, userStatus,image,thumb_image,device_token;

    public UserProfile() {

    }

    public UserProfile(String userName, String userEmail, String userStatus, String image, String thumb_image) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userStatus = userStatus;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public UserProfile(String userName, String userEmail, String userStatus, String image, String thumb_image, String device_token) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userStatus = userStatus;
        this.image = image;
        this.thumb_image = thumb_image;
        this.device_token = device_token;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
