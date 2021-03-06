package com.example.personal.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mChatToolbar;

    private DatabaseReference mRootRef;
    String userName;

    private TextView mTitleView;
    private TextView mLastseenView;
    private CircleImageView mProfileImage;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageView mChatAddBtn;
    private ImageView mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_MESSAGES_TO_LOAD=10;
    private int mCurrentPage=1;

    private static final int GALLERY_PICK=1;

    private StorageReference mImageStorage;

    private ProgressDialog loadingBar;

    //New Solution
    private int itemPos=0;
    private String mLastKey="";
    private String mPrevKey="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatToolbar=(Toolbar)findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mCurrentUserId=mAuth.getCurrentUser().getUid();
        mImageStorage=FirebaseStorage.getInstance().getReference().child("message_images");

        loadingBar=new ProgressDialog(this);

        mChatUser=getIntent().getStringExtra("user_id");
        userName=getIntent().getStringExtra("user_name");
        getSupportActionBar().setTitle(userName);

        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.chat_custom_bar,null);

        actionBar.setCustomView(action_bar_view);

        mTitleView=(TextView)findViewById(R.id.custom_bar_title);
        mLastseenView=(TextView)findViewById(R.id.custom_bar_seen);
        mProfileImage=(CircleImageView)findViewById(R.id.custom_bar_image);

        mChatAddBtn=(ImageButton)findViewById(R.id.chat_add_btn);
        mChatSendBtn=(ImageButton)findViewById(R.id.chat_send_btn);
        mChatMessageView=(EditText)findViewById(R.id.chat_message_view);

        mAdapter=new MessageAdapter(messagesList);

        mMessagesList=(RecyclerView)findViewById(R.id.messages_list);
        mRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        mLinearLayout=new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        loadMessages();


        mTitleView.setText(userName);

        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online=dataSnapshot.child("online").getValue().toString();
                String image=dataSnapshot.child("thumb_image").getValue().toString();

                if(online.equals("true")){
                    mLastseenView.setText("Online");
                }
                else{
                    GetTimeAgo getTimeAgo=new GetTimeAgo();
                    long lastTime=Long.parseLong(online);
                    String lastSeenTime=getTimeAgo.getTimeAgo(lastTime,getApplicationContext());

                    mLastseenView.setText("last seen "+lastSeenTime);
                }

                if(!image.equals("default")){
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.calendar).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference user=mRootRef.child("Chat").child(mCurrentUserId);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mChatUser)){
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);


                    Map chatUserMap=new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser,chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId,chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Log.d("CHAT_LOG",databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(galleryIntent,GALLERY_PICK);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos=0;
                //messagesList.clear();
                loadMoreMessages();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null){
            loadingBar.setTitle("Sending Image");
            loadingBar.setMessage("Please Wait for a while");
            loadingBar.show();

            Uri imageUri=data.getData();

            final String current_user_ref="messages/" + mCurrentUserId + "/" + mChatUser;
            final String chat_user_ref="messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push=mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();
            final String push_id=user_message_push.getKey();

            StorageReference filePath=mImageStorage.child("message_images").child(push_id + ".jpg");

            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        String download_url=task.getResult().getDownloadUrl().toString();
                        Map messageMap=new HashMap();
                        messageMap.put("message",download_url);
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from",mCurrentUserId);

                        Map messageUserMap=new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id,messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id,messageMap);

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null){
                                    Log.d("CHAT_LOG",databaseError.getMessage().toString());
                                }
                                mChatMessageView.setText("");
                                loadingBar.dismiss();
                            }
                        });
                        Toast.makeText(ChatActivity.this,"Image sent Successfully",Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else{
                        loadingBar.dismiss();
                        Toast.makeText(ChatActivity.this,"Image not sent.Try Again",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef=mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery=messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message=dataSnapshot.getValue(Messages.class);
                String messageKey=dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++ , message);
                }
                else{
                    mPrevKey=mLastKey;
                }

                if(itemPos==1){
                    //String messageKey=dataSnapshot.getKey();
                    mLastKey=messageKey;
                }


                Log.d("TOTAL KEYS" , "Last Key:" + mLastKey + "| Prev Key" + mPrevKey + "| Message Key:" + messageKey);

                mAdapter.notifyDataSetChanged();

                //mMessagesList.scrollToPosition(messagesList.size()-1);

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10,0);            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {
        DatabaseReference messageRef=mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery=messageRef.limitToLast(mCurrentPage*TOTAL_MESSAGES_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message=dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos==1){
                    String messageKey=dataSnapshot.getKey();
                    mLastKey=messageKey;
                    mPrevKey=messageKey;
                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size()-1);

                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message=mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){
            String current_user_ref="messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref="messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push=mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();
            String push_id=user_message_push.getKey();

            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);

            Map messageUserMap=new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id,messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id,messageMap);

            mChatMessageView.getText().clear();

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}
