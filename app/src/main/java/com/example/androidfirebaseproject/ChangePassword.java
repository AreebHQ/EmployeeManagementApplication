package com.example.androidfirebaseproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.employee.EmployeeLanding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChangePassword extends AppCompatActivity {

    //Navigation
    NavigationView navView;
    DrawerLayout drawer;
    Toolbar toolbar;
    TextView navFirstName;
    TextView navEmail;
    TextView navCompany;
    CircleImageView navProfilePic;
    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    //UI
    Button btn_confirm;
    EditText et_password;
    TextView tv_email, tv_status;
    // Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        // Navigation Drawer
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
        fAuth = FirebaseAuth.getInstance();
        // UI
        btn_confirm = findViewById(R.id.btn_confirm);
        et_password = findViewById(R.id.et_password);
        tv_status = findViewById(R.id.tv_status);
        tv_email = findViewById(R.id.tv_email);
        tv_email.setText(fUser.getEmail());

        // Button Confirm
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassword(v);
            }
        });

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
    }

    public void UpdatePassword(View v) {
       // Password Validation
        String newPassword = et_password.getText().toString();
        if (TextUtils.isEmpty(newPassword))
        {
            et_password.setError("Password Required.");
            tv_status.setText("Password Required");
            return;
        }
        if (newPassword.length() > 15 || newPassword.length() < 8)
        {
            et_password.setError("Invalid Password Length.");
            tv_status.setText("Invalid Password Length");
            return;
        }
        EditText currentPass = new EditText(v.getContext());
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(v.getContext());
        // Dialogbox visuals
        confirmationDialog.setTitle("Confirm Identity");
        confirmationDialog.setMessage("Enter Current Password");
        confirmationDialog.setView(currentPass);
        confirmationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AuthCredential credential = EmailAuthProvider.getCredential(fUser.getEmail(), currentPass.getText().toString().trim());
                // Prompt the user to re-provide their sign-in credentials
                fUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("TAG", "User re-authenticated.");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        fUser.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            tv_status.setText("Password Updated Successfully!");
                                            Toast.makeText(getApplicationContext(),"Password Update Successful",Toast.LENGTH_LONG).show();
                                            Log.d("UpdatePassword", "User password updated.");
                                            Intent intent = new Intent(getApplicationContext(), EmployeeLanding.class);
                                            intent.putExtra("userName", navUserName);
                                            intent.putExtra("userEmail", navEmailAddress);
                                            intent.putExtra("userProfilePicUri", navProfilePicUri);
                                            intent.putExtra("userCompanyName", navUserCompany);
                                            startActivity(intent);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tv_status.setText("Password Updated Error! Please Try Again.");
                                Toast.makeText(getApplicationContext(),"Password Update Error",Toast.LENGTH_LONG).show();
                                Log.d("UpdatePassword", "Error! Password was not changed.");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Authentication Failed!",Toast.LENGTH_LONG).show();
                        Log.d("UpdatePassword", "Error! Authentication Failed");
                    }
                });
            }
        });
        confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ChangePassword.this, "Update Cancelled", Toast.LENGTH_LONG).show();
            }
        });
        confirmationDialog.create().show();
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