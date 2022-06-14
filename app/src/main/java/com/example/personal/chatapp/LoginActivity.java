package com.example.personal.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class LoginActivity extends AppCompatActivity {

    private EditText Name,Password;
    private Button btnLogin;
    private TextView tv1,tvForgotPassword,textView3;
    private int count=5;
    private Toolbar mToolbar;
    private String name,password;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUserDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpUIViews();
        mToolbar=(Toolbar)findViewById(R.id.login_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(this);

        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=Name.getText().toString();
                password=Password.getText().toString();
                if(name.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Please fill All Details.",Toast.LENGTH_LONG).show();
                }
                validate(name,password);
            }
        });


        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,PasswordActivity.class));
            }
        });

    }

    public void setUpUIViews(){
        Name=(EditText)findViewById(R.id.etUserName);
        Password=(EditText)findViewById(R.id.etUserPassword);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        tv1=(TextView)findViewById(R.id.tv1);
        tvForgotPassword=(TextView)findViewById(R.id.tvForgotPassword);
        textView3=(TextView)findViewById(R.id.textView3);
    }

    public void validate(String userName,String Passwd){

        progressDialog.setMessage("Logging You In...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(userName,Passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Login Successful!!",Toast.LENGTH_LONG).show();
                    checkEmailVerification();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Login Failed!!",Toast.LENGTH_LONG).show();
                    count--;
                    tv1.setText("No. of attempts remaining: "+count);
                    progressDialog.dismiss();
                    if(count==0){
                        btnLogin.setEnabled(false);
                    }

                    ProgressDialog pd=new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Please Check Your Internet Connection Once.\nor\nEnter Correct Password..");
                    pd.setCancelable(true);
                    pd.show();
                }
            }
        });
    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag=firebaseUser.isEmailVerified();

        if(emailflag){
            String current_user_id=firebaseUser.getUid();
            String deviceToken= FirebaseInstanceId.getInstance().getToken();
            mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this,"Verify your Email..",Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
    }
}
