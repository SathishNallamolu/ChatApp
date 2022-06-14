package com.example.personal.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class StatusActivity extends AppCompatActivity {

    private EditText mStatus;
    private EditText mUserName;
    private TextView mEmail;
    private Button mSaveBtn;
    ProgressDialog progressDialog;
    String image;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String status_value=getIntent().getStringExtra("status_value");
        String status_user=getIntent().getStringExtra("status_user");
        String status_mail=getIntent().getStringExtra("status_mail");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        final DatabaseReference databaseReference=firebaseDatabase.getReference().child("Users").child(firebaseUser.getUid());

        mStatus=(EditText)findViewById(R.id.status_input);
        mUserName=(EditText)findViewById(R.id.status_user_name);
        mEmail=(TextView)findViewById(R.id.status_mail);
        mSaveBtn=(Button)findViewById(R.id.status_save_btn);

        mStatus.setText(status_value);
        mUserName.setText(status_user);
        mEmail.setText(status_mail);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                mUserName.setText(userProfile.getUserName());
                mStatus.setText(userProfile.getUserStatus());
                mEmail.setText(userProfile.getUserEmail());
                image=dataSnapshot.child("image").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StatusActivity.this,databaseError.getCode(),Toast.LENGTH_LONG).show();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status=mStatus.getText().toString();
                String name=mUserName.getText().toString();
                String mail=mEmail.getText().toString();
                String device_token= FirebaseInstanceId.getInstance().getToken();
                UserProfile userProfile=new UserProfile(name,mail,status,image,"default",device_token);
                databaseReference.setValue(userProfile);
                finish();
            }
        });
    }
}
