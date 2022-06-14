package com.example.personal.chatapp;

public class Users {
    public String userName;
    public String image;
    public String userStatus;
    public String thumb_image;

    public Users(){

    }

    public Users(String userName, String image, String userStatus, String thumb_image) {
        this.userName = userName;
        this.image = image;
        this.userStatus = userStatus;
        this.thumb_image = thumb_image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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


//ProfileActivity code

           /*mFriendReqDatabase.child(firebaseUser.getUid()).child(user_id).child("request_type").setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendReqDatabase.child(user_id).child(firebaseUser.getUid()).child("request_type").setValue("Received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String,String> notificationData=new HashMap<>();
                                        notificationData.put("from",firebaseUser.getUid());
                                        notificationData.put("type","request");

                                        mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mCurrent_state="req_sent";
                                                mProfileSendRegBtn.setText("Cancel Friend Request");
                                                Toast.makeText(ProfileActivity.this,"FriendRequest sent Successfully..",Toast.LENGTH_LONG).show();
                                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                                mDeclineBtn.setEnabled(false);
                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this,"Error in Sending FriendRequest..",Toast.LENGTH_LONG).show();
                            }
                            mProfileSendRegBtn.setEnabled(true);
                        }
                    });*/
