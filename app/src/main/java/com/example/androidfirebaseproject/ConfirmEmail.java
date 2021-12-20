package com.example.androidfirebaseproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ConfirmEmail extends AppCompatActivity {

    // Button signIn
    Button btn_signIn;
    //Firebase authentication
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        btn_signIn = findViewById(R.id.button3);

        // Button signIn
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginPageOriginal.class));
            }
        });
    }

    // Reset Password Method
    public void callResendCode(View view) {
        FirebaseUser fuser = fAuth.getCurrentUser();
        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ConfirmEmail.this,"Verification Code Sent Successfully!", Toast.LENGTH_SHORT).show();
                Log.d("Resend Verification", "Resend Verification Code Successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ConfirmEmail.this,"Verification Code Error!", Toast.LENGTH_SHORT).show();
                Log.d("Resend Verification", "Resend Verification CodeError!");
            }
        });
    }
}