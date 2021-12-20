package com.example.androidfirebaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class TerminatedEmployee extends AppCompatActivity {

    FirebaseAuth  fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminated_employee);


        // Setting display data after 5 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                fAuth.signOut();

            }
        }, 5000);


    }
}