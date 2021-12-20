package com.example.androidfirebaseproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployerProfile extends AppCompatActivity {

    CircleImageView cv_profilePicture;
    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    Button btn_updateProfile;
    //Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();
    // UI
    TextView tv_skip;
    ImageView iv_skip;
    TextView nameHeader, tv_companyName;
    EditText profileFirstName,profileLastName, profileContact, profileAddress, profileEmail, profileDOB;
    String firstName, lastName, contact, address, email, dateOfBirth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        nameHeader = findViewById(R.id.profileNameHeader);
        cv_profilePicture = findViewById(R.id.CV_profilePicture);
        profileFirstName = findViewById(R.id.profileFirstName);
        profileLastName = findViewById(R.id.profileLastName);
        profileContact = findViewById(R.id.profileContact);
        profileAddress = findViewById(R.id.profileAddress);
        profileEmail = findViewById(R.id.profileEmail);
        profileDOB = findViewById(R.id.profileDOB);
        btn_updateProfile = findViewById(R.id.profileButton);
        tv_companyName = findViewById(R.id.tv_companyName);
        tv_skip = findViewById(R.id.tv_skip);
        iv_skip = findViewById(R.id.iv_skip);

        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("userName");
            navEmailAddress = extras.getString("userEmail");
            navProfilePicUri = extras.getString("userProfilePicUri");
            navUserCompany = extras.getString("userCompanyName");
        }
        Picasso.get().load(navProfilePicUri).into(cv_profilePicture);
        nameHeader.setText(navUserName);
        // Firebase Reference - getting user data
        DocumentReference docRef = fStore.collection("Users").document(fUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String fname = document.getString("Firstname");
                        if (!TextUtils.isEmpty(fname)) {
                            profileFirstName.setText(document.getString("Firstname"));
                        }
                        String lname = document.getString("Lastname");
                        if (!TextUtils.isEmpty(lname)) {
                            profileLastName.setText(document.getString("Lastname"));
                        }
                        String email = document.getString("Email");
                        if (!TextUtils.isEmpty(email)) {
                            profileEmail.setText(document.getString("Email"));
                        }
                        String dob = document.getString("DateOfBirth");
                        if (!TextUtils.isEmpty(dob)) {
                            profileDOB.setText(document.getString("DateOfBirth"));
                        }
                        String addr = document.getString("Address");
                        if (!TextUtils.isEmpty(addr)) {
                            profileAddress.setText(document.getString("Address"));
                        }
                        String cont = document.getString("Contact");
                        if (!TextUtils.isEmpty(cont)) {
                            profileContact.setText(document.getString("Contact"));
                        }
                        String cName = document.getString("CompanyName");
                        if (!TextUtils.isEmpty(cName)) {
                            tv_companyName.setText(document.getString("CompanyName"));
                        }
                    }
                } else {
                    Log.d("User Information", "User information was not found.");
                }
            }
        });
        // Setting skip functions
        iv_skip.setOnClickListener(v-> { skip();});
        tv_skip.setOnClickListener(v-> { skip();});
        //Button to update profile
        btn_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firstName = profileFirstName.getText().toString();
                lastName = profileLastName.getText().toString();
                contact = profileContact.getText().toString();
                address = profileAddress.getText().toString();
                email = profileEmail.getText().toString();
                dateOfBirth = profileDOB.getText().toString();

                if (TextUtils.isEmpty(firstName)) {
                    profileFirstName.setError("First Name Required.");
                    return;
                }  if (TextUtils.isEmpty(lastName)) {
                    profileLastName.setError("Last Name Required.");
                    return;
                }  if (TextUtils.isEmpty(contact)) {
                    profileContact.setError("Contact Required.");
                    return;
                }  if (TextUtils.isEmpty(address)) {
                    profileAddress.setError("Address Required.");
                    return;
                }  if (TextUtils.isEmpty(email)) {
                    profileEmail.setError("Email Required.");
                    return;
                }  if (TextUtils.isEmpty(dateOfBirth)) {
                    profileDOB.setError("Date of birth Required.");
                    return;
                }

                // Alertbox
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(view.getContext());
                // Dialogbox visuals
                confirmationDialog.setTitle("Confirm Changes");
                confirmationDialog.setMessage("Please select to confirm changes");
                // if YES
                confirmationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        docRef.update("Firstname", profileFirstName.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "User First Name updated.");
                                    }
                                });
                        docRef.update("Lastname", profileLastName.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "User Last Name updated.");
                                    }
                                });
                        docRef.update("Address", profileAddress.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "User Address updated.");
                                    }
                                });
                        docRef.update("Contact", profileContact.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "User Contact updated.");
                                    }
                                });
                        docRef.update("Email", profileEmail.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "User Email updated.");
                                    }
                                });
                        docRef.update("DateOfBirth", profileDOB.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "User Date of Birth updated.");
                                    }
                                });
                        Toast.makeText(EmployerProfile.this, "Profile Information Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
                        intent.putExtra("userName", navUserName);
                        intent.putExtra("userEmail", navEmailAddress);
                        intent.putExtra("userProfilePicUri", navProfilePicUri);
                        intent.putExtra("userCompanyName", navUserCompany);
                        startActivity(intent);
                    }
                });

                confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EmployerProfile.this, "Update Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                confirmationDialog.create().show();
            }
        });
    }

    public void skip()
    {
        firstName = profileFirstName.getText().toString();
        lastName = profileLastName.getText().toString();
        contact = profileContact.getText().toString();
        address = profileAddress.getText().toString();
        email = profileEmail.getText().toString();
        dateOfBirth = profileDOB.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            profileFirstName.setError("First Name Required.");
            Toast.makeText(EmployerProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
            return;
        }  if (TextUtils.isEmpty(lastName)) {
            profileLastName.setError("Last Name Required.");
            Toast.makeText(EmployerProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
            return;
    }  if (TextUtils.isEmpty(contact)) {
            profileContact.setError("Contact Required.");
            Toast.makeText(EmployerProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
            return;
    }  if (TextUtils.isEmpty(address)) {
            profileAddress.setError("Address Required.");
            Toast.makeText(EmployerProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
            return;
    }  if (TextUtils.isEmpty(email)) {
            profileEmail.setError("Email Required.");
            Toast.makeText(EmployerProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
            return;
    }  if (TextUtils.isEmpty(dateOfBirth)) {
            profileDOB.setError("Date of birth Required.");
            Toast.makeText(EmployerProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
            return;
    }
        Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
        intent.putExtra("userName", navUserName);
        intent.putExtra("userEmail", navEmailAddress);
        intent.putExtra("userProfilePicUri", navProfilePicUri);
        intent.putExtra("userCompanyName", navUserCompany);
        startActivity(intent);
    }

}