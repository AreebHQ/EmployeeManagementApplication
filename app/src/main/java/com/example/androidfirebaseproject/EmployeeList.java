package com.example.androidfirebaseproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employee.EmployeeLanding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EmployeeList extends AppCompatActivity {

    RecyclerView recyclerView;
    EmployeeAdapter employeeAdapter;
    Button btn_getdata, btn_goBack;
    ArrayList<String> employeeImageList = new ArrayList<>();
    ArrayList<String> employeeNamesList = new ArrayList<>();
    // Naviation drawer
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    // Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        recyclerView = findViewById(R.id.recycleView_employee_list);
        btn_getdata  = findViewById(R.id.btn_retireveEmployees);
        btn_goBack = findViewById(R.id.btn_goBack);

        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("userName");
            navEmailAddress = extras.getString("userEmail");
            navProfilePicUri = extras.getString("userProfilePicUri");
            navUserCompany = extras.getString("userCompanyName");
        }
        //Firebase reference
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
                                        if (TextUtils.isEmpty(compName) || TextUtils.isEmpty(compCode)) {
                                            continue;
                                        }
                                        Log.d("CompanyName ======= ", compName);
                                        if (compName.equals(navUserCompany)) {

                                            String name = document.getString("Firstname") + " " + document.getString("Lastname");
                                            String imageUrl = document.getString("ProfileURL");
                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                            Log.d("TAG", name);
                                            Log.d("TAG", document.getString("ProfileURL"));
                                            employeeNamesList.add(name);

                                            if (TextUtils.isEmpty(imageUrl)) {
                                                employeeImageList.add("imageUrl");
                                                Log.d("PROFILE IMAGE", "Profile Image Not Found");
                                            } else {
                                                employeeImageList.add(imageUrl);
                                            }
                                        }
                                  }
                               }
                        } else {

                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // Getting Employee list button
        btn_getdata.setOnClickListener(v->{

            // In case of no employees
            if (employeeNamesList.size() == 0)
            {
                Toast.makeText(EmployeeList.this,"No Employee Found",Toast.LENGTH_LONG).show();
            }
            else {
                // Calling employee list adapter
                employeeAdapter = new EmployeeAdapter(this,employeeNamesList, employeeImageList,navUserCompany,EmployeeList.this);
                recyclerView.setAdapter(employeeAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            }
        });

        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            }
        });
    }
}


