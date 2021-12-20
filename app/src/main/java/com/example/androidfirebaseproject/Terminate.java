package com.example.androidfirebaseproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Terminate extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Serializable {

    //Navigation
    NavigationView navView;
    DrawerLayout drawer;
    Toolbar toolbar;
    TextView navFirstName;
    TextView navEmail;
    TextView navCompany;
    CircleImageView navProfilePic;
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    // Setting data
    ArrayList<String> employeeImageList = new ArrayList<>();
    ArrayList<String> employeeList = new ArrayList<>();
    ArrayList<String> employeeUIDList = new ArrayList<>();
    ArrayList<String> employeeEmailList = new ArrayList<>();
    ArrayAdapter<String> spinnerArrayAdapter;
    Spinner employeeSpinner;
    ProgressBar progress;
    ImageView iv_employeeImage, iv_skip;
    String companyCode;
    TextView tv_empName, tv_empEmail, tv_employeeUid, tv_companyName, tv_skip;
    Button btn_delete;
    int pos = 0;

    // Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate);

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
        progress = findViewById(R.id.progressBar);
        iv_employeeImage = findViewById(R.id.iv_employeeImage);
        tv_empEmail = findViewById(R.id.tv_email);
        tv_empName = findViewById(R.id.tv_employeeName);
        tv_employeeUid = findViewById(R.id.tv_employeeUID);
        tv_companyName = findViewById(R.id.tv_companyName);
        iv_skip = findViewById(R.id.iv_skipTerminate);
        tv_skip = findViewById(R.id.tv_skipTerminate);
        btn_delete = findViewById(R.id.btn_delete);

        employeeSpinner = findViewById(R.id.sp_dropDownEmployeeList);
        employeeSpinner.setOnItemSelectedListener(this);

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

        // getting uid as company code
        companyCode = fuser.getUid();

        //Getting list of employees
        getEmployeeList();

        // Setting skip functions
        tv_skip.setOnClickListener(v->{ startActivity(new Intent(getApplicationContext(), Admin_Homepage.class));   });
        iv_skip.setOnClickListener(v->{ startActivity(new Intent(getApplicationContext(), Admin_Homepage.class));   });

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
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(v.getContext());
                // Dialogbox visuals
                confirmationDialog.setTitle("Confirm Delete");
                confirmationDialog.setMessage("Confirm to delete this user?");
                confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Calling deleteUser", " Deleting user");
                       deleteUser();
                    }
                });

                confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                confirmationDialog.create().show();
            }
        });
    }


    public void getEmployeeList()
    {
        fStore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                 String employeeStatus = document.getString("isAdmin");
                                if (!TextUtils.isEmpty(employeeStatus) && !employeeStatus.equals("terminated") && !employeeStatus.equals("admin")) {
                                    String compName = document.getString("CompanyName");
                                    String compCode = document.getString("CompanyCode");
                                    String uid = document.getString("Uid");
                                    String url = document.getString("ProfileURL");
                                    String email = document.getString("Email");
                                    if (TextUtils.isEmpty(compName) || TextUtils.isEmpty(compCode) || TextUtils.isEmpty(uid)) {
                                        continue;
                                    }
                                    if (TextUtils.isEmpty(url)) {
                                        url = "";
                                    }

                                    if (compName.equals(navUserCompany) && compCode.equals(companyCode)) {

                                        Log.d("CompanyName ======= ", compName);
                                        Log.d("CompanyCode ======= ", compCode);
                                        Log.d("EmployeeUID ======= ", uid);
                                        Log.d("EmployeeUID ======= ", url);
                                        String name = document.getString("Firstname") + " " + document.getString("Lastname");
                                        Log.d("Employee Name", "" + name);
                                        employeeUIDList.add(uid.trim());
                                        employeeList.add(name);
                                        employeeImageList.add(url);
                                        employeeEmailList.add(email);
                                    }
                                }
                            }
                            spinnerArrayAdapter = new ArrayAdapter<>
                                    (getApplicationContext(), android.R.layout.simple_spinner_item, employeeList);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            employeeSpinner.setAdapter(spinnerArrayAdapter);

                        }
                        else {

                            Log.d("getEmployeeList", "Error getting employee list: ", task.getException());
                        }
                    }
                });
    }

    public void deleteUser()
    {
        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(employeeUIDList.get(pos));

        //Storing user info in map
        Map<String, Object> info = new HashMap<>();
        info.put("isAdmin","terminated");

        // Saving / Merging data with the existing clock in punch
        docReference.set(info, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("deleteUser","Terminated Employee Successfully" );
                Toast.makeText(getApplicationContext(),"Terminated Employee Successfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("deleteUser"," Failure Terminating Employee" );
                Toast.makeText(getApplicationContext(),"Failure Terminating Employee", Toast.LENGTH_LONG).show();
            }
        });

    }

    // Drawer
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

    // Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        pos = position;
        tv_empName.setText(employeeList.get(position));
        tv_empEmail.setText(employeeEmailList.get(position));
        tv_employeeUid.setText(employeeUIDList.get(position));
        Picasso.get().load(employeeImageList.get(position)).into(iv_employeeImage);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}