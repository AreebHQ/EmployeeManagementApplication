package com.example.androidfirebaseproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetQRCode extends AppCompatActivity {
    // Navigation Drawer
    NavigationView navView;
    DrawerLayout drawer;
    TextView navFirstName;
    TextView navEmail;
    TextView navCompany;
    CircleImageView navProfilePic;
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    Toolbar toolbar;
    //UI
    Button btn_continue;
    EditText et_date, et_QRvalue;
    TextView tv_companyName, tv_qrWebLink;
    String strDate, strQRvalue;

    // Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    // MonthReference
    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_qrcode);
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
        //UI
        btn_continue = findViewById(R.id.btn_continue);
        et_date = findViewById(R.id.et_date);
        et_QRvalue = findViewById(R.id.et_QRcode);
        tv_companyName = findViewById(R.id.tv_CompanyName);
        tv_qrWebLink = findViewById(R.id.tv_qrWebLink);

        // Setting QRCode website link
        tv_qrWebLink.setMovementMethod(LinkMovementMethod.getInstance());

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

        // Calling setQRValue methods
        btn_continue.setOnClickListener(v->{setQRValue();});
    }

    // Setting QR value in firebase
    public void setQRValue()
    {
        strDate = et_date.getText().toString();
        strQRvalue = et_QRvalue.getText().toString();
        // Validation
        if (TextUtils.isEmpty(strDate))
        {
            et_date.setError("Enter valid date");
            return;
        }
        if (TextUtils.isEmpty(strQRvalue))
        {
            et_QRvalue.setError("Enter valid QRvalue");
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date currentDate = new Date();
        String strCurrentDate = dateFormat.format(currentDate);

        String []date = strDate.split("-");
        // Getting Month Year and Time separately
        String strMon = date[0];
        String strDay = date[1];
        String strYear = date[2];

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];

        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid())
                .collection("QRCodeAttendance").document(strYear).collection(monthName).document(strDate);
        //Storing user info in map
        Map<String, Object> QRInfo = new HashMap<>();
        QRInfo.put("DateCreated", strCurrentDate);
        QRInfo.put("Day", strDay);
        QRInfo.put("QRValue",strQRvalue);
        QRInfo.put("Method","QRCode");

        docReference.set(QRInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                Log.d("Successful","Saved  Date: "+ strDate);
                Log.d("Successful","Saved  QRValue: "+ strQRvalue);
                Toast.makeText(getApplicationContext(),"QR Attendance Setup Successfully",Toast.LENGTH_LONG).show();
                clearFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","QR Information not saved");
            }
        });
    }

    public void clearFields()
    {
        et_date.setText("");
        et_QRvalue.setText("");
        startActivity(new Intent(getApplicationContext(),Manage.class));
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