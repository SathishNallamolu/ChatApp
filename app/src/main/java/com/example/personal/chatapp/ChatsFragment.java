package com.example.personal.chatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mChatsList;
    private RecyclerView.Adapter adapter;
    private View mMainView;

    private FirebaseAuth mAuth;
    private DatabaseReference mfriendsDatabase;
    private DatabaseReference mUsersDatabase;

    String mCurrent_user_id;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView=inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();
        mfriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");

        mChatsList=(RecyclerView)mMainView.findViewById(R.id.chats_list);
//        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

       /* FirebaseRecyclerOptions<Chats> options=new FirebaseRecyclerOptions.Builder<Chats>()
                .setQuery(mfriendsDatabase,Chats.class)
                .build();

        FirebaseRecyclerAdapter<Chats,ChatsViewHolder> adapter=new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Chats model) {
                final String usersIDs=getRef(position).getKey();
                mUsersDatabase.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("image")){
                            final String retImage=dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(retImage).into(holder.profileImage);
                        }
                        final String retName=dataSnapshot.child("userName").getValue().toString();
                        final String retStatus=dataSnapshot.child("userStatus").getValue().toString();

                        holder.username.setText(retName);
                        holder.userStatus.setText("Last seen:"+"\n"+"Date "+"Time");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
                return new ChatsViewHolder(view);
            }
        };
        mChatsList.setAdapter(adapter);
        adapter.startListening();*/
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView userStatus,username;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.user_single_image);
            userStatus=itemView.findViewById(R.id.user_single_status);
            username=itemView.findViewById(R.id.user_single_name);
        }
    }
}
