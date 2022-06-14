package com.example.personal.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private EditText etPasswordEmail;
    private Button btnPasswordReset;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        etPasswordEmail = (EditText) findViewById(R.id.etPasswordEmail);
        btnPasswordReset = (Button) findViewById(R.id.btnPasswordReset);

        firebaseAuth = FirebaseAuth.getInstance();

        btnPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=etPasswordEmail.getText().toString();

                if(user.isEmpty()){
                    Toast.makeText(PasswordActivity.this,"Please Enter a valid Registered Email Address.. ",Toast.LENGTH_LONG).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordActivity.this,"Password Reset Email has been Sent..",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(PasswordActivity.this,MainActivity.class));
                            }
                            else{
                                Toast.makeText(PasswordActivity.this,"Error in Sending Password Reset Email...",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}

