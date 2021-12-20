package com.example.employee;

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

import com.example.androidfirebaseproject.LoginPageOriginal;
import com.example.androidfirebaseproject.R;
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


public class EmployeeChangePassword extends AppCompatActivity {

    //Navigation
    NavigationView navView;
    DrawerLayout drawer;
    Toolbar toolbar;
    TextView navFirstName;
    TextView navEmail;
    CircleImageView navProfilePic;
    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName;

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
        setContentView(R.layout.activity_employee_change_password);

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
        fAuth = FirebaseAuth.getInstance();
        btn_confirm = findViewById(R.id.btn_confirm);
        et_password = findViewById(R.id.et_password);
        tv_status = findViewById(R.id.tv_status);
        tv_email = findViewById(R.id.tv_email);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("thisName");
            navEmailAddress = extras.getString("thisEmail");
            navProfilePicUri = extras.getString("ProfilePicUri");
        }
        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);
        tv_email.setText(fUser.getEmail());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassword(v);
            }
        });

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
    }

    public void UpdatePassword(View v) {

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
                                            intent.putExtra("thisName", navUserName);
                                            intent.putExtra("thisEmail", navEmailAddress);
                                            intent.putExtra("ProfilePicUri", navProfilePicUri);
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
                Toast.makeText(EmployeeChangePassword.this, "Update Cancelled", Toast.LENGTH_LONG).show();
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