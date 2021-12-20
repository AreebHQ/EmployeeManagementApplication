package com.example.androidfirebaseproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUp extends AppCompatActivity {

    //UI
    EditText companyinfo, firstname, lastname, pass, email;
    Button signup;
    String companyName,companyCodeinfo, firstName, lastName, e_mail, password, user;
    CheckBox termsandconditions;
    SwitchCompat switchButton;
    TextView companyHeader, tvPassword;

    // Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tvPassword = findViewById(R.id.textViewPass);
        companyHeader = findViewById(R.id.company);
        companyinfo = findViewById(R.id.companyinfo);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        pass = findViewById(R.id.password);
        email = findViewById(R.id.email);
        termsandconditions = findViewById(R.id.termscheckBox);
        signup = findViewById(R.id.btn_signup);
        switchButton = findViewById(R.id.switchCompatone);
        // Setting user as Admin - only employer (admins) can sign up
        user = "admin";
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tvPassword.getLayoutParams();

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (switchButton.isChecked()) {
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(v.getContext());
                    // Dialogbox visuals
                    confirmationDialog.setTitle("Employee Sign Up ");
                    confirmationDialog.setMessage("For Employee SignUp Please Contact Your Administrator or Supervisor.");
                    confirmationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), LoginPageOriginal.class));
                        }
                    });
                    confirmationDialog.create().show();
                }
            }
        });

        //Firebase authentication
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Upon clicking sign up button
        signup.setOnClickListener((e) -> {

            // getting user input into string and int objects
            companyCodeinfo = companyinfo.getText().toString();
            firstName = firstname.getText().toString();
            lastName = lastname.getText().toString();
            e_mail = email.getText().toString().trim();
            password = pass.getText().toString().trim();

            if (TextUtils.isEmpty(firstName)) {
                firstname.setError("First Name Required.");
                return;
            }
            if (firstName.length() < 3 || firstName.length() > 30) {
                firstname.setError("First Name not valid!");
                return;
            }
            if (TextUtils.isEmpty(lastName)) {
                lastname.setError("First Name Required.");
                return;
            }

            if (TextUtils.isEmpty(e_mail)) {
                email.setError("Email Required.");
                return;
            }
            if (!(e_mail.contains("@")) || !(e_mail.contains(".com")) && !(e_mail.contains(".edu")) ) {
                email.setError("Invalid Email!");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                pass.setError("Password Required.");
                return;
            }
            if (password.length() > 15 || password.length() < 8) {
                pass.setError("Password length must be grater than 8 and lower then 15 characters");
                return;
            }
            if (TextUtils.isEmpty(companyCodeinfo)) {
                companyinfo.setError("Field Required.");
                return;
            }

            if (!termsandconditions.isChecked()) {
                termsandconditions.setError("Must Accept Terms & Conditions");
                return;
            }
            Log.d("New User Data", " Data Validated Successfully");

            // creating user in firebase
            fAuth.createUserWithEmailAndPassword(e_mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("New User Registration", "Registration Successful!");
                        FirebaseUser fuser = fAuth.getCurrentUser();

                        // Creating documents reference for user in Firabase database
                        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid());

                        //Storing user info in map
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("CompanyName", companyName);
                        userInfo.put("Firstname", firstName);
                        userInfo.put("Lastname", lastName);
                        userInfo.put("Email", e_mail);
                        // specifying the user status :  = admin
                        userInfo.put("isAdmin", user);
                        //if user is admin - get company name
                        userInfo.put("CompanyCode", companyName);
                        //Setting user profile as null which will be updated later
                        userInfo.put("ProfileURL","Null");
                        userInfo.put("Contact","Null");
                        userInfo.put("Address","Null");
                        userInfo.put("DateOfBirth","Null");

                        docReference.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("New User Data", " User: " + firstName + " Data stored in database Successfully.");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("New User Data", " User: " + firstName + " Data storage in database Failed.");
                            }
                        });

                        fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(SignUp.this, "Verification Email Sent!", Toast.LENGTH_SHORT).show();
                                Log.d("New User Verification", "Verification Email Sent!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(SignUp.this, "Verification Email Error!", Toast.LENGTH_SHORT).show();
                                Log.d("New User Verification", "Verification Email Error!");
                            }
                        });

                        //callin saed data to move to mainActivity - new user created
                        startActivity(new Intent(getApplicationContext(), ConfirmEmail.class));

                    } else {

                        Toast.makeText(SignUp.this, "Error Creating Account!", Toast.LENGTH_SHORT).show();
                        Log.d("New User Registration", "Error Creating Account");
                    }
                }
            });
        });

        Log.d("Process Complete", "Nothing Changed");
    }

}
