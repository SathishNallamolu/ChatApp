package com.example.personal.chatapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateBtn;
    private TextView textView2;
    private Toolbar mToolbar;
    String etName,etEmail,etPassword,etStatus;
    private ProgressDialog mRegProgress;

    private FirebaseAuth firebaseAuth;
    DatabaseReference myRef;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();

        mDisplayName=(EditText)findViewById(R.id.reg_display_name);
        mEmail=(EditText)findViewById(R.id.reg_email);
        mPassword=(EditText)findViewById(R.id.reg_password);
        mCreateBtn=(Button)findViewById(R.id.reg_create_btn);
        textView2=(TextView)findViewById(R.id.textView2);
        mToolbar=(Toolbar)findViewById(R.id.register_toolbar);
        mRegProgress=new ProgressDialog(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String display_name=mDisplayName.getText().toString();
                    String email=mEmail.getText().toString();
                    String password=mPassword.getText().toString();

                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendEmailVerification();
                                sendUserData();
                                AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("REGISTRATION FORM");
                                builder.setMessage("You are Successfully Registerd\nPlease Wait for Some Moment...");
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                });
                                builder.show();
                                Toast.makeText(RegisterActivity.this,"Successfully Registered",Toast.LENGTH_LONG).show();

                                firebaseAuth.signOut();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this,"Please Check your Details and Tryagain.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }


    public Boolean validate() {
        Boolean result=false;
        etName=mDisplayName.getText().toString();
        etEmail=mEmail.getText().toString();
        etPassword=mPassword.getText().toString();

        if(etName.isEmpty() || etEmail.isEmpty() || etPassword.isEmpty()){
            Toast.makeText(RegisterActivity.this,"Please Fill All Details",Toast.LENGTH_LONG).show();
        }
        else{
            result=true;
        }
        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Error in sending VerificationEmail",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void sendUserData() {

        firebaseDatabase=FirebaseDatabase.getInstance();
        myRef=FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
        //String device_token= FirebaseInstanceId.getInstance().getToken();

        UserProfile userProfile=new UserProfile(etName,etEmail,"Hey there! I am using ChatApp.","default","default");

        /*HashMap<String,String> userMap=new HashMap<>();
        userMap.put("userEmail:",etEmail);
        userMap.put("userName:",etName);
        userMap.put("userStatus:","Hey there! I am using ChatApp.");
        userMap.put("image","default");
        userMap.put("thumb_image:","default");*/
        myRef.setValue(userProfile);
    }
}
