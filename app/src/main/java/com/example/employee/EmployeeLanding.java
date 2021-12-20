package com.example.employee;

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

import com.example.androidfirebaseproject.LoginPageOriginal;
import com.example.androidfirebaseproject.R;
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

public class EmployeeLanding extends AppCompatActivity {

    // Navigation Drawer
    NavigationView navView;
    DrawerLayout drawer;
    TextView navFirstName;
    TextView navEmail;
    CircleImageView navProfilePic, mainProfilePic;

    LinearLayout btn_Pay, btn_Attendance, btn_ClockinOut, btn_Settings, btn_LogOut;
    TextView tv_homepageUsername, tv_companyName;
    Toolbar toolbar;
    String companyName;

    String navProfilePicUri, navEmailAddress, navUserName;
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_landing);
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
        mainProfilePic = findViewById(R.id.homepageProfilePic);
        // UI
        btn_Pay = findViewById(R.id.btn_Pay);
        btn_ClockinOut = findViewById(R.id.btn_ClockinOut);
        btn_Attendance = findViewById(R.id.btn_attendance);
        btn_Settings = findViewById(R.id.btn_Setting);
        btn_LogOut = findViewById(R.id.btn_SignOut);
        tv_homepageUsername = findViewById(R.id.homepageUsername);
        tv_companyName = findViewById(R.id.companyName);

        //Setting profile picture and data
        setNavigationDrawerData();
        setNavUserData();

        Log.d("User Name", " Username: " + navUserName);

        navView.setCheckedItem(R.id.navHome);
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

        btn_Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PayData.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });
        btn_Attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeAttendance.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });
        btn_ClockinOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeClock.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });
        btn_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeSettings.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            }
        });
        mainProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmployeeProfile.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
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
                        navFirstName.setText(navUserName);
                        tv_homepageUsername.setText(navUserName);
                        String cName = document.getString("CompanyName");
                        companyName = cName;
                        tv_companyName.setText(companyName);
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

        item.setChecked(true);
       switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}