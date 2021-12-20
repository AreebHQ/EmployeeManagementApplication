package com.example.androidfirebaseproject;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.employee.EmployeeLanding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Hire extends AppCompatActivity {
    // UI
    ImageView uploadPic, iv_skip;
    EditText et_firstName, et_lastName, et_address, et_contact, et_DOB, et_email, et_employeeID, et_payRate, et_EmployeePosition,et_hireDate;
    TextView tv_companyName, tv_skip;
    Uri imageUri;
    Button btn_continue;
    String companyCode; // adminUID
    boolean isLogin = true;

    //Firebase authentication
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference reference;

    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire);

        uploadPic = findViewById(R.id.addProfilePicture);
        et_firstName = findViewById(R.id.hire_firstName);
        et_lastName = findViewById(R.id.hire_lastName);
        et_address = findViewById(R.id.hire_address);
        et_contact = findViewById(R.id.hire_contact);
        et_DOB = findViewById(R.id.hire_DOB);
        et_email = findViewById(R.id.hire_employeeEmail);
        et_employeeID = findViewById(R.id.hire_employeeID);
        et_payRate = findViewById(R.id.hire_payRate);
        et_EmployeePosition = findViewById(R.id.hire_employeePosition);
        et_hireDate = findViewById(R.id.hire_employeeHireDate);
        btn_continue = findViewById(R.id.btn_continue);
        tv_companyName = findViewById(R.id.tv_hireCompanyName);
        iv_skip = findViewById(R.id.iv_skip);
        tv_skip = findViewById(R.id.tv_skip);


        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("userName");
            navEmailAddress = extras.getString("userEmail");
            navProfilePicUri = extras.getString("userProfilePicUri");
            navUserCompany = extras.getString("userCompanyName");
        }
        // Setting company name
        tv_companyName.setText(navUserCompany);
        // Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        getAdminUid();


        // Skip methods
        iv_skip.setOnClickListener(v->{callSkip();});
        tv_skip.setOnClickListener(v->{callSkip();});

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = et_firstName.getText().toString().trim();
                String lastName = et_lastName.getText().toString().trim();
                String address = et_address.getText().toString();
                String contact = et_contact.getText().toString().trim();
                String dateOfBirth = et_DOB.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String payRate = et_payRate.getText().toString().trim();
                String employeePosition = et_EmployeePosition.getText().toString().trim();
                String hireDate = et_hireDate.getText().toString().trim();

                if (imageUri == null) {
                    Toast.makeText(Hire.this, "Please Select Image", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(firstName)) {
                    et_firstName.setError("First Name Required.");
                    return;
                }
                if (firstName.length() < 3 || firstName.length() > 15) {
                    et_firstName.setError("First Name not valid! Must be between 3-15 characters");
                    return;
                }
                if (TextUtils.isEmpty(lastName)) {
                    et_lastName.setError("Last Name Required.");
                    return;
                }
                if (lastName.length() < 3 || lastName.length() > 15) {
                    et_lastName.setError("Last Name not valid! Must be between 3-15 characters");
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    et_address.setError("Address Required.");
                    return;
                }
                if (TextUtils.isEmpty(contact)) {
                    et_contact.setError("Contact No. Required.");
                    return;
                }
                if (TextUtils.isEmpty(dateOfBirth)) {
                    et_DOB.setError("DOB Required.");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    et_email.setError("Email Required.");
                    return;
                }
                if (!(email.contains("@")) || !(email.contains(".com")) && !(email.contains(".edu"))) {
                    et_email.setError("Invalid Email");
                    return;
                }
                if (TextUtils.isEmpty(payRate)) {
                    et_payRate.setError("Pay Rate Required.");
                    return;
                }
                if (TextUtils.isEmpty(employeePosition)) {
                    et_EmployeePosition.setError("Position Required.");
                    return;
                }
                if (TextUtils.isEmpty(hireDate)) {
                    et_hireDate.setError("Hire Date Required.");
                    return;
                }

                // Setting temporary password as employee last name + birth year
                String[] dob = dateOfBirth.split("-");
                String tempPassword = lastName + dob[2];
                Log.d("New User Password", "Password: " + tempPassword);

                delay();

                // creating user in firebase // using id instead
                fAuth.createUserWithEmailAndPassword(email, tempPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("New User Registration", "Registration Successful!");
                        FirebaseUser fuser = fAuth.getCurrentUser();

                        String employeeID = et_employeeID.getText().toString().trim();
                        if (TextUtils.isEmpty(employeeID)) {
                            employeeID = "not assigned";
                        }
                        // Creating documents reference for user in Firabase database
                        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid());

                        String newUserUid = fAuth.getCurrentUser().getUid();
                        //Storing user info in map
                        Map<String, Object> UserInfo = new HashMap<>();
                        UserInfo.put("CompanyName", navUserCompany);
                        UserInfo.put("CompanyCode", companyCode);
                        UserInfo.put("Uid",newUserUid);
                        UserInfo.put("Firstname", firstName);
                        UserInfo.put("Lastname", lastName);
                        UserInfo.put("Contact", contact);
                        UserInfo.put("Address", address);
                        UserInfo.put("Email", email);
                        UserInfo.put("DateOfBirth", dateOfBirth);
                        UserInfo.put("EmployeeID", employeeID);
                        UserInfo.put("Position", employeePosition);
                        UserInfo.put("HireDate", hireDate);
                        UserInfo.put("PayRate", payRate);
                        UserInfo.put("isAdmin", "employee");


                        docReference.set(UserInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast toast =  Toast.makeText(Hire.this, "User :" + firstName + " Data Saved", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                                toast.show();
                                Log.d("New User Data", " User: " + firstName + " Data stored in database Successfully.");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast toast =  Toast.makeText(Hire.this, "Error! :" + firstName + " Data Not Saved", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                                toast.show();
                                Log.d("New User Data", " User: " + firstName + " Data storage in database Failed.");
                            }
                        });


                        fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //  Toast.makeText(Hire.this, "Verification Email Sent!", Toast.LENGTH_SHORT).show();
                                Snackbar.make(v, "Account Created! \n Verification Email Sent!", Snackbar.LENGTH_LONG).show();
                                Log.d("New User Verification", "Verification Email Sent!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(Hire.this, "Verification Email Error!", Toast.LENGTH_SHORT).show();
                                Log.d("New User Verification", "Verification Email Error!");
                            }
                        });
                        uploadToFirebase(imageUri);

                    } else {

                        Toast.makeText(Hire.this, "Error Creating Account!", Toast.LENGTH_SHORT).show();
                        Log.d("New User Registration", "Error Creating Account");
                    }
                });

                Log.d("At bottom", " Got information successfully");
                FirebaseAuth.getInstance().signOut();

                Log.d("Signing out", " signing out has been successfully completed ");
                //Getting data from saved preference file
                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                String xemail1 = sharedPreferences.getString("email", "");
                String xpass1 = sharedPreferences.getString("password", "");
                fAuth.signInWithEmailAndPassword(xemail1, xpass1);
                isLogin = true;
            }
        });

    }


    public void delay()
    {
        // Signing out after 5 seconds - after getting admin UID
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // getting admin uid to use as company code
                isLogin = false;
             //   getAdminUid();
                FirebaseAuth.getInstance().signOut();
                Log.d("SignOut Current", "Current user signed out");
            }
        }, 3000);
    }

    public void getAdminUid()
    {
        FirebaseUser fuser = fAuth.getCurrentUser();
        companyCode = fuser.getUid();
        Log.d("CompanyCode", "Company Code: " + companyCode);
    }

    public void clearData()
    {
        et_firstName.setText("");
        et_lastName.setText("");
        et_address.setText("");
        et_contact.setText("");
        et_DOB.setText("");
        et_email.setText("");
        et_employeeID.setText("");
        et_payRate.setText("");
        et_EmployeePosition.setText("");
        et_hireDate.setText("");
        uploadPic.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            uploadPic.setImageURI(imageUri);
        }
    }

    //Profile pic upload
    private void uploadToFirebase(Uri uri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore ffStore = FirebaseFirestore.getInstance();
        StorageReference fileRef = reference.child("profileImages").child( uid + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Create data object to add field in firebase data table
                                Map<String,Object> updateData = new HashMap<>();
                                updateData.put("ProfileURL",uri.toString());
                                ffStore.collection("Users").document(uid).set(updateData, SetOptions.merge());
                                Log.d("SavingURL", "Saving URl to database" + uri );
                                Log.d("uploadToFirebase", "Successful " );
                                //if image upload successful - show toast
                                Toast.makeText(Hire.this, "Upload Successful",Toast.LENGTH_LONG).show();
                                Log.d("uploadToFirebase", "Image set from URL" );
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // if image upload failed - show toast
                Toast.makeText(Hire.this, "Upload Error",Toast.LENGTH_LONG).show();
            }
        });
        clearData();
    }


    private void callSkip()
    {
        clearData();
        Log.d("Signing out", " signing out has been successfully completed ");
        if (isLogin)
        {
            Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
            intent.putExtra("userName", navUserName);
            intent.putExtra("userEmail", navEmailAddress);
            intent.putExtra("userProfilePicUri", navProfilePicUri);
            intent.putExtra("userCompanyName", navUserCompany);
            startActivity(intent);

        } else {
            //Getting data from saved preference file
            SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            String xemail1 = sharedPreferences.getString("email", "");
            String xpass1 = sharedPreferences.getString("password", "");
            fAuth.signInWithEmailAndPassword(xemail1, xpass1);

            // Signing out after 2 seconds - after getting admin UID
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
                    intent.putExtra("userName", navUserName);
                    intent.putExtra("userEmail", navEmailAddress);
                    intent.putExtra("userProfilePicUri", navProfilePicUri);
                    intent.putExtra("userCompanyName", navUserCompany);
                    startActivity(intent);
                }
            }, 3000);
        }
    }


    // to get selected image extension : jpeg, png etc
    private String getFileExtension(Uri mUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(mUri));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isLogin)
        {
            Log.d("OnResume", "getting signin information");
            SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            String xemail = sharedPreferences.getString("email", "");
            String xpass = sharedPreferences.getString("password", "");
            fAuth.signInWithEmailAndPassword(xemail, xpass);
        }

    }
}