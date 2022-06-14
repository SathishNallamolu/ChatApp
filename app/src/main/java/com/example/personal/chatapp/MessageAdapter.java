package com.example.personal.chatapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

/**
 * Created by Personal on 10-11-2018.
 */

public class  MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private DatabaseReference mUserDatabase;
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    public DatabaseReference mUserDatabaseRef;

    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList=mMessageList;
    }

    String name;

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(v);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView profileImage;
        public TextView nameText;
        //private TextView timeText;
        public ImageView messageImage;

        public MessageViewHolder(View view){
            super(view);

            messageText=(TextView) view.findViewById(R.id.message_text_layout);
            profileImage=(CircleImageView)view.findViewById(R.id.message_profile_layout);
            nameText=(TextView)view.findViewById(R.id.name_text_layout);
            //timeText=(TextView)view.findViewById(R.id.time_text_layout);
            messageImage=(ImageView)view.findViewById(R.id.message_image_layout);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        mAuth=FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        String token= FirebaseInstanceId.getInstance().getToken();
        UserProfile userProfile=new UserProfile();
        String dtoken=userProfile.getDevice_token();
        Messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("userName").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                holder.nameText.setText(name);
                Picasso.get().load(image).placeholder(R.drawable.calendar).into(holder.profileImage);
                //holder.profileImage.setPadding(100,-3,0,0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (message_type.equals("text")) {
            holder.messageText.setVisibility(View.VISIBLE);
            //holder.messageText.setText(c.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);
            if(from_user==(current_user_id)){
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setLayoutDirection(RIGHT);
            }
            else{
                holder.messageText.setBackgroundResource(R.drawable.message_text_background2);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setLayoutDirection(LEFT);
            }
            holder.messageText.setText(c.getMessage());

        }
        else {
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.messageImage.setVisibility(View.VISIBLE);
            Picasso.get().load(c.getMessage()).placeholder(R.drawable.calendar).into(holder.messageImage);
        }
    }
        @Override
        public int getItemCount () {
            return mMessageList.size();
        }
}
