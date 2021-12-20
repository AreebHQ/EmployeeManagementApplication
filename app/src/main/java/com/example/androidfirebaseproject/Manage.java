package com.example.androidfirebaseproject;


import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Manage extends AppCompatActivity {
    //Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    // Navigation Drawer
    NavigationView navView;
    DrawerLayout drawer;
    TextView navFirstName;
    TextView navEmail;
    TextView navCompany;
    CircleImageView navProfilePic;
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    Toolbar toolbar;
    // UI
    LinearLayout ll_setQrCode, ll_editTime,ll_editProfile, ll_editPassword;
    TextView tv_companyName;
    Button btn_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        // Setting Navigation Drawer
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
        navCompany = header.findViewById(R.id.nav_header_company_name);
        ll_setQrCode = findViewById(R.id.ll_setQRCode);
        ll_editTime = findViewById(R.id.ll_editTime);
        ll_editProfile = findViewById(R.id.ll_editProfile);
        ll_editPassword = findViewById(R.id.ll_editPassword);
        btn_continue = findViewById(R.id.btn_continue);
        tv_companyName = findViewById(R.id.tv_hireCompanyName);

        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("userName");
            navEmailAddress = extras.getString("userEmail");
            navProfilePicUri = extras.getString("userProfilePicUri");
            navUserCompany = extras.getString("userCompanyName");
        }
        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);
        navCompany.setText(navUserCompany);
        tv_companyName.setText(navUserCompany);

        ll_setQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetQRCode.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            }
        });


        ll_editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditTime.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            }
        });

        ll_editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            }
        });

        ll_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployerProfile.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            }
        });

        btn_continue.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
            intent.putExtra("userName", navUserName);
            intent.putExtra("userEmail", navEmailAddress);
            intent.putExtra("userProfilePicUri", navProfilePicUri);
            intent.putExtra("userCompanyName", navUserCompany);
            startActivity(intent);

        });

        // Drawer Menu
        navView.setCheckedItem(R.id.navManage);
        navView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navHome) {
                Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navProfile) {
                Intent intent = new Intent(getApplicationContext(), EmployerProfile.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navReports) {
                Intent intent = new Intent(getApplicationContext(), WeeklyPayroll.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navHire) {
                Intent intent = new Intent(getApplicationContext(), Hire.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navTerminate) {
                Intent intent = new Intent(getApplicationContext(), Terminate.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navEmployeeList) {
                Intent intent = new Intent(getApplicationContext(), EmployeeList.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navManage) {
                Intent intent = new Intent(getApplicationContext(), Manage.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navSetting) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
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