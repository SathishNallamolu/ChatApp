package com.example.personal.chatapp;

/**
 * Created by Personal on 31-10-2018.
 */

public class Friends {
    String date,key;

    public Friends(){

    }

    public Friends(String date, String key) {
        this.date = date;
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
