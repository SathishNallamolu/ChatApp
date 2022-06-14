package com.example.personal.chatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView myRequestsList;
    private View myMainView;

    private DatabaseReference FriendRequestsReference;
    private FirebaseAuth mAuth;
    public String online_user_id;
    private DatabaseReference UsersReference;
    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView=inflater.inflate(R.layout.fragment_requests, container, false);
        myRequestsList=(RecyclerView)myMainView.findViewById(R.id.requests_list);

        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        FriendRequestsReference= FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(online_user_id);
        UsersReference=FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef=FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        myRequestsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests,RequestViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(
                        Requests.class,
                        R.layout.friend_requests_all_users_layuot,
                        RequestsFragment.RequestViewHolder.class,
                        FriendRequestsReference
                ){
                @Override
                protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position) {
                    final String list_users_id=getRef(position).getKey();
                    //Toast.makeText(getContext(),list_users_id,Toast.LENGTH_LONG).show();

                    DatabaseReference get_type_type=getRef(position).child("request_type").getRef();
                    get_type_type.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String request_type=dataSnapshot.getValue().toString();
                                if(request_type.equals("Received")){
                                    //Toast.makeText(getContext(),"Successfully",Toast.LENGTH_LONG).show();
                                    UsersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final String userName=dataSnapshot.child("userName").getValue().toString();
                                            final String userThumb=dataSnapshot.child("thumb_image").getValue().toString();
                                            final String status=dataSnapshot.child("userStatus").getValue().toString();

                                            viewHolder.setUserName(userName);
                                            viewHolder.setThumbImage(userThumb,getContext());
                                            viewHolder.setUserStatus(status);

                                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CharSequence options[]=new CharSequence[]{"Accept FriendRequest","Cancel FriendRequest"};

                                                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                    builder.setTitle("Friend Request Options");
                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if(which==0){
                                                                final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

                                                                FriendsDatabaseRef.child(online_user_id).child(list_users_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        FriendsDatabaseRef.child(list_users_id).child(online_user_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                Toast.makeText(getContext(),"FriendRequest Accepted Successfully",Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                            if(which==1){
                                                                FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(getContext(),"FriendRequest Cancelled Successfully",Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else if(request_type.equals("Sent")){
                                    //Toast.makeText(getContext(),"Successfullynot",Toast.LENGTH_LONG).show();
                                    Button request_sent_btn=viewHolder.mView.findViewById(R.id.request_accept_btn);
                                    request_sent_btn.setText("REQUEST SENT");
                                    viewHolder.mView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                                    UsersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final String userName = dataSnapshot.child("userName").getValue().toString();
                                            final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                            final String status = dataSnapshot.child("userStatus").getValue().toString();

                                            viewHolder.setUserName(userName);
                                            viewHolder.setThumbImage(userThumb, getContext());
                                            viewHolder.setUserStatus(status);

                                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CharSequence options[]=new CharSequence[]{"Cancel FriendRequest"};

                                                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                    builder.setTitle("Friend Request Sent");
                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if(which==0){
                                                                FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(getContext(),"FriendRequest Cancelled Successfully",Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
        };
        myRequestsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public RequestViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setUserName(String userName) {
            TextView userNameDisplay=(TextView)mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(userName);
        }

        public void setThumbImage(final String userThumb,final Context ctx) {
            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.request_profile_image);
            Picasso.get().load(userThumb).placeholder(R.drawable.calendar).into(userImageView);
        }

        public void setUserStatus(String status) {
            TextView userStatusDisplay=(TextView)mView.findViewById(R.id.request_profile_status);
            userStatusDisplay.setText(status);
        }
    }
}
