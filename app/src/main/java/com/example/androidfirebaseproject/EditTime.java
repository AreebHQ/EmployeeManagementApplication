package com.example.androidfirebaseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditTime extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Serializable {

    // Navigation Drawer
    NavigationView navView;
    DrawerLayout drawer;
    TextView navFirstName;
    TextView navEmail;
    TextView navCompany;
    CircleImageView navProfilePic;
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    Toolbar toolbar;

    // Setting time
    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
    ArrayAdapter<String> spinnerArrayAdapter;
    ArrayList<String> employeeList = new ArrayList<>();
    ArrayList<String> employeeUIDList = new ArrayList<>();
    Spinner employeeSpinner;
    String companyCode;
    int pos = 0;
    //String strClockin;
    // String strClockout;
    String strTimerStart;
    String strTimerStop;
    String clockInShift = "AM";
    String clockOutShift = "PM";
    Switch sw_clockIn, sw_clockOut;

    // UI
    TextView tv_companyName;
    EditText et_date, et_clockIn, et_clockOut;
    Button btn_setChange, btn_check, btn_continue;

    //Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_time);
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
        // UI
        tv_companyName = findViewById(R.id.tv_CompanyName);
        et_date = findViewById(R.id.et_date);
        et_clockIn = findViewById(R.id.et_clockIn);
        et_clockOut = findViewById(R.id.et_clockOut);
        btn_setChange = findViewById(R.id.btn_setChange);
        btn_check = findViewById(R.id.btn_check);
        btn_continue = findViewById(R.id.btn_continue);
        sw_clockIn = findViewById(R.id.sw_clockIn);
        sw_clockOut = findViewById(R.id.sw_clockOut);
        // Spinner
        employeeSpinner = findViewById(R.id.dropDownEmployeeList);
        employeeSpinner.setOnItemSelectedListener(this);
        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("userName");
            navEmailAddress = extras.getString("userEmail");
            navProfilePicUri = extras.getString("userProfilePicUri");
            navUserCompany = extras.getString("userCompanyName");
        }
        // Navigation Drawer
        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);
        navCompany.setText(navUserCompany);
        tv_companyName.setText(navUserCompany);

        // getting uid as company code
        companyCode = fuser.getUid();

        // getting employee list in spinner
        getEmployeeList();

        //Setting switches
        sw_clockIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(sw_clockIn.isChecked())
                {
                    clockInShift = "PM";
                    sw_clockIn.setText("PM");
                    Log.d("Check", "Clockin Status: " + clockInShift);
                }
                else {
                    clockInShift = "AM";
                    sw_clockIn.setText("AM");
                    Log.d("Check", "Clockin Status: " + clockInShift);
                }

            }
        });

        sw_clockOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(sw_clockOut.isChecked())
                {
                    clockOutShift = "AM";
                    sw_clockOut.setText("AM");
                    Log.d("Check", "Clockin Status: " + clockOutShift);
                }
                else {
                    clockOutShift = "PM";
                    sw_clockOut.setText("PM");
                    Log.d("Check", "Clockin Status: " + clockOutShift);
                }

            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmployeeHours(pos);
            }
        });

        btn_setChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setEmployeeTime(pos);
            }
        });

        btn_continue.setOnClickListener(v->{startActivity(new Intent(getApplicationContext(),Manage.class));});

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
                                if (!TextUtils.isEmpty(employeeStatus) && !employeeStatus.equals("terminated")) {
                                    String compName = document.getString("CompanyName");
                                    String compCode = document.getString("CompanyCode");
                                    String uid = document.getString("Uid");
                                    if (TextUtils.isEmpty(compName) || TextUtils.isEmpty(compCode) || TextUtils.isEmpty(uid)) {
                                        continue;
                                    }
                                    if (compName.equals(navUserCompany) && compCode.equals(companyCode)) {

                                        Log.d("CompanyName ======= ", compName);
                                        Log.d("CompanyCode ======= ", compCode);
                                        Log.d("EmployeeUID ======= ", uid);
                                        String name = document.getString("Firstname") + " " + document.getString("Lastname");
                                        Log.d("Employee Name", "" + name);
                                        employeeUIDList.add(uid.trim());
                                        employeeList.add(name);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        pos = position;
        String strDate = et_date.getText().toString();
        if(TextUtils.isEmpty(strDate))
        {
            et_date.setError("Enter Date First");
            return;
        }
        String[] date = strDate.split("-");
        String strMon = date[0];
        String strDay = date[1];
        String strYear = date[2];

        // Checking the right items
        Log.d("Year", "Year is: " + strYear);
        Log.d("Month", "Month is: " + strMon);
        Log.d("Day", "Day is: " + strDay);

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        // Setting month name and year as firebase document name ex: nov, jan
        Log.d("MonthName","Month is: "+ monthName);
        // Firebase Database
        DocumentReference docRef = fStore.collection("Users").document(employeeUIDList.get(position))
                .collection("Attendance").document(strYear).collection(monthName).document(strDate);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                            String clockIn = document.getString("ClockIn");
                            String clockOut = document.getString("ClockOut");
                            Log.d("Time Found: ", "ClockIn: " + clockIn + " ClockOut: " + clockOut);
                            // In case of missing timer time - ask user to clock in again
                            if (!TextUtils.isEmpty(clockIn))
                            {
                                et_clockIn.setText(document.getString("ClockIn"));
                            }
                           if (!TextUtils.isEmpty(clockOut))
                            {
                                et_clockOut.setText(document.getString("ClockOut"));
                            } else {
                                et_clockIn.setText("Missing");
                                et_clockOut.setText("Missing");
                            }
                    } else {
                        Log.d("Date Error", "No Date Found");
                        et_clockIn.setText("Missing");
                        et_clockOut.setText("Missing");
                    }
                } else {
                    Log.d("Data Error", "Retrieving Data Failed with ", task.getException());
                    et_clockIn.setText("Missing");
                    et_clockOut.setText("Missing");
                }
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setEmployeeTime(int pos)
    {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String strDate = et_date.getText().toString();
        Date currentDate = new Date();
        String currentStrDate = dateFormat.format(currentDate);
        // Validating Date
        if(TextUtils.isEmpty(strDate))
        {
            et_date.setError("Invalid Date");
            Toast.makeText(getApplicationContext(),"Error! Invalid Date", Toast.LENGTH_LONG).show();
            return;
        }
        if(et_clockIn.getText().toString().contains("AM") || et_clockOut.getText().toString().contains("PM"))
        {
            Toast.makeText(getApplicationContext(),"Error! Please remove AM/PM! \n Use switch to determine the shift",Toast.LENGTH_LONG).show();
            return;
        }
        // Splitting date into DD MM YYYY
        String[] date = strDate.split("-");
        String strMon = date[0];
        String strDay = date[1];
        String strYear = date[2];

        // Checking the right items
        Log.d("Year", "Year is: " + strYear);
        Log.d("Month", "Month is: " + strMon);
        Log.d("Day", "Day is: " + strDay);

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        // Setting month name and year as firebase document name ex: nov, jan
        Log.d("MonthName","Month is: "+ monthName);
        String strClockin = et_clockIn.getText().toString().trim();
        String strClockout = et_clockOut.getText().toString().trim();

        // Setting timer time
        if (!TextUtils.isEmpty(strClockin))
        {
           strTimerStart= strClockin+":00 "+clockInShift;

        } else {
            strTimerStart="";
        }
        // Setting timer time
        if (!TextUtils.isEmpty(strClockout))
        {
            strTimerStop= strClockout+":00 "+clockOutShift;

        } else {
            strTimerStop="";
        }

        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(employeeUIDList.get(pos))
                .collection("Attendance").document(strYear).collection(monthName).document(strDate);

        //Storing user info in map
        Map<String, Object> punchInfo = new HashMap<>();
        punchInfo.put("ClockIn",strClockin+" "+clockInShift);
        punchInfo.put("ClockOut",strClockout+" "+clockOutShift);
        punchInfo.put("TimerStart",strTimerStart);
        punchInfo.put("TimerStop",strTimerStop);
        punchInfo.put("LastEdit",currentStrDate);

        // Saving / Merging data with the existing clock in punch
        docReference.set(punchInfo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("setEmployeeTime","Successful Edited ClockIn Time" );
                Log.d("setEmployeeTime","Successful Edited ClockOut Time" );
                Toast.makeText(getApplicationContext(),"Time Edited Successfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("setEmployeeTime"," Failure Setting ClockIn Time" );
                Toast.makeText(getApplicationContext(),"Failure Editing Clock Time", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getEmployeeHours(int pos){

            String strDate = et_date.getText().toString();
            if(TextUtils.isEmpty(strDate))
            {
                et_date.setError("Invalid Date");
                Toast.makeText(getApplicationContext(),"Error! Invalid Date", Toast.LENGTH_LONG).show();
                return;
            }
            String[] date = strDate.split("-");
            String strMon = date[0];
            String strDay = date[1];
            String strYear = date[2];

            // Checking the right items
            Log.d("Year", "Year is: " + strYear);
            Log.d("Month", "Month is: " + strMon);
            Log.d("Day", "Day is: " + strDay);

            // Getting month name from month array
            String monthName = months[Integer.parseInt(strMon)];
            // Setting month name and year as firebase document name ex: nov, jan
            Log.d("MonthName","Month is: "+ monthName);

            // Firebase Database
            DocumentReference docRef = fStore.collection("Users").document(employeeUIDList.get(pos))
                    .collection("Attendance").document(strYear).collection(monthName).document(strDate);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String clockIn = document.getString("ClockIn");
                            String clockOut = document.getString("ClockOut");
                            Log.d("Time Found: ", "ClockIn: " + clockIn + " ClockOut: " + clockOut);
                            // In case of missing timer time - ask user to clock in again
                            if (!TextUtils.isEmpty(clockIn))
                            {
                                et_clockIn.setText(document.getString("ClockIn"));
                            }
                            if (!TextUtils.isEmpty(clockOut))
                            {
                                et_clockOut.setText(document.getString("ClockOut"));
                            }
                            else {
                                et_clockIn.setText("Missing");
                                et_clockOut.setText("Missing");
                            }
                            Toast.makeText(getApplicationContext(),"Refreshed", Toast.LENGTH_LONG).show();

                        } else {
                            Log.d("Date Error", "No Date Found");
                            Toast.makeText(getApplicationContext(),"Error! No Date Found", Toast.LENGTH_LONG).show();
                            et_clockIn.setText("Missing");
                            et_clockOut.setText("Missing");
                        }
                    } else {
                        Log.d("Data Error", "Retrieving Data Failed with ", task.getException());
                        Toast.makeText(getApplicationContext(),"Error! Failed Retrieving Data", Toast.LENGTH_LONG).show();
                        et_clockIn.setText("Missing");
                        et_clockOut.setText("Missing");
                    }
                }
            });
    }
}