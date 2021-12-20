package com.example.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.androidfirebaseproject.LoginPageOriginal;
import com.example.androidfirebaseproject.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeSettings extends AppCompatActivity {
   //Navigation
    NavigationView navView;
    DrawerLayout drawer;
    Toolbar toolbar;
    TextView navFirstName;
    TextView navEmail;
    CircleImageView navProfilePic;

    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName;

    ImageView iv_editProfile, iv_changePassword;
    TextView tv_editProfile, tv_changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_settings);
        navView = findViewById(R.id.nav_view_main_menu);
        drawer = findViewById(R.id.drawerLayoutMainMenu);



        toolbar = findViewById(R.id.app_Bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);
        View header = navView.getHeaderView(0);
        navFirstName = header.findViewById(R.id.nav_header_first_name);
        navEmail = header.findViewById(R.id.nav_header_email);
        navProfilePic = header.findViewById(R.id.nav_header_picture);

        iv_editProfile = findViewById(R.id.iv_editProfile);
        iv_changePassword = findViewById(R.id.iv_editPassword);
        tv_changePassword = findViewById(R.id.tv_changePass);
        tv_editProfile = findViewById(R.id.tv_editProfileInfo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("thisName");
            navEmailAddress = extras.getString("thisEmail");
            navProfilePicUri = extras.getString("ProfilePicUri");
        }

        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);


        navView.setCheckedItem(R.id.navEmpSetting);
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navHome) {
                Intent intent = new Intent(getApplicationContext(), EmployeeLanding.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpPay) {
                Intent intent = new Intent(getApplicationContext(), PayData.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpProfile) {
                Intent intent = new Intent(getApplicationContext(), EmployeeProfile.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpAttendance) {
                Intent intent = new Intent(getApplicationContext(), EmployeeAttendance.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpClock) {
                Intent intent = new Intent(getApplicationContext(), EmployeeClock.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpSetting) {
                Intent intent = new Intent(getApplicationContext(), EmployeeSettings.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navSignOut) {
                // Signing user out from app
                FirebaseAuth.getInstance().signOut();

                // Calling login activity
                startActivity(new Intent(getApplicationContext(), LoginPageOriginal.class));
                finish();

            }
            DrawerLayout drawer = findViewById(R.id.drawerLayoutMainMenu);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });


        iv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeProfile.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });

        iv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeChangePassword.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });


        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeChangePassword.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });


        tv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeProfile.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });





    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}