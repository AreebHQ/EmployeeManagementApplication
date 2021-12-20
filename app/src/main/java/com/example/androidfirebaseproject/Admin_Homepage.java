package com.example.androidfirebaseproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Admin_Homepage extends AppCompatActivity {

        // Navigation Drawer
        NavigationView navView;
        DrawerLayout drawer;
        TextView navFirstName;
        TextView navEmail;
        TextView navCompany;
        CircleImageView navProfilePic;
        String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
        Toolbar toolbar;
        //Display
        CircleImageView mainProfilePic;
        LinearLayout btn_Reports, btn_Hire, btn_EmployeeList, btn_Settings, btn_LogOut,btn_Terminate;
        TextView tv_homepageUsername, tv_companyName;
        //Firebase
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_homepage);
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

            // UI Components
            mainProfilePic = findViewById(R.id.homepageProfilePic);
            btn_Reports = findViewById(R.id.btn_Reports);
            btn_Hire = findViewById(R.id.btn_Hire);
            btn_Settings = findViewById(R.id.btn_Setting);
            btn_EmployeeList = findViewById(R.id.btn_employeeList);
            btn_Terminate = findViewById(R.id.btn_terminate);
            btn_LogOut = findViewById(R.id.btn_SignOut);
            tv_homepageUsername = findViewById(R.id.homepageUsername);
            tv_companyName = findViewById(R.id.companyName);

            //Setting profile picture and data
            setNavigationDrawerData();
            setNavUserData();

            // Drawer Menu
            navView.setCheckedItem(R.id.navHome);
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
            }); // Navigation Menu Finished

            //Button Manage
            btn_Reports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Manage.class);
                    intent.putExtra("userName", navUserName);
                    intent.putExtra("userEmail", navEmailAddress);
                    intent.putExtra("userProfilePicUri", navProfilePicUri);
                    intent.putExtra("userCompanyName", navUserCompany);
                    startActivity(intent);
                }
            });
            //Button Hire
            btn_Hire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Hire.class);
                    intent.putExtra("userName", navUserName);
                    intent.putExtra("userEmail", navEmailAddress);
                    intent.putExtra("userProfilePicUri", navProfilePicUri);
                    intent.putExtra("userCompanyName", navUserCompany);
                    startActivity(intent);
                }
            });
            //Button EmployeeList
            btn_EmployeeList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), EmployeeList.class);
                    intent.putExtra("userName", navUserName);
                    intent.putExtra("userEmail", navEmailAddress);
                    intent.putExtra("userProfilePicUri", navProfilePicUri);
                    intent.putExtra("userCompanyName", navUserCompany);
                    startActivity(intent);
                }
            });
            btn_Settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Settings.class);
                    intent.putExtra("userName", navUserName);
                    intent.putExtra("userEmail", navEmailAddress);
                    intent.putExtra("userProfilePicUri", navProfilePicUri);
                    intent.putExtra("userCompanyName", navUserCompany);
                    startActivity(intent);
                }
            });
            mainProfilePic.setOnClickListener(new View.OnClickListener() {
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
            btn_Terminate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Terminate.class);
                    intent.putExtra("userName", navUserName);
                    intent.putExtra("userEmail", navEmailAddress);
                    intent.putExtra("userProfilePicUri", navProfilePicUri);
                    intent.putExtra("userCompanyName", navUserCompany);
                    startActivity(intent);
                }
            });

            btn_LogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Signing user out from app
                    FirebaseAuth.getInstance().signOut();
                    // Calling login activity
                    startActivity(new Intent(getApplicationContext(),LoginPageOriginal.class));
                    finish();
                }
            });
        }

        // Setting user data in navigation drawer
        private void setNavUserData()
        {
            // Getting the User's company name
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            navEmailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            navEmail.setText(navEmailAddress);

            DocumentReference docRef = fStore.collection("Users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userName = document.getString("Firstname") + " " + document.getString("Lastname");
                            navUserName = userName;
                            Log.d("User Name", "Username Found: " + navUserName);
                            String cName = document.getString("CompanyName");
                            navUserCompany = cName;
                            Log.d("User Company", "Company Found: "+ cName);
                            navCompany.setText(navUserCompany);
                            tv_companyName.setText(navUserCompany);
                            navFirstName.setText(navUserName);
                            tv_homepageUsername.setText(navUserName);
                        }
                    } else {
                        Log.d("User Name", "Failed to get user name ", task.getException());
                    }
                }
            });

            Log.d("User Name", " Username: " + navUserName);
            Log.d("User Email", " Email: " + navEmailAddress);
        }


        // setting profile picture and username
        private void setNavigationDrawerData()
        {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference fileRef = reference.child("profileImages").child( uid + ".jpg");
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    navProfilePicUri = uri.toString();
                    Picasso.get().load(uri).into(mainProfilePic);
                    Picasso.get().load(uri).into(navProfilePic);
                    Log.d("setProfilePic", "Profile Image set from Firebase URL" );
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mainProfilePic.setImageResource(R.drawable.ic_baseline_person_24);
                    navProfilePic.setImageResource(R.drawable.ic_baseline_person_24);
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