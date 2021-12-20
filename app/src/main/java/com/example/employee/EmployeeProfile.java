package com.example.employee;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfirebaseproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeProfile extends AppCompatActivity {



    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName;
    Button btn_updateProfile;
    CircleImageView cv_profilePicture;
    Uri imageUri;

    // Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();
    StorageReference reference = FirebaseStorage.getInstance().getReference();

    // UI
    TextView tv_skip;
    ImageView iv_skip;
    TextView nameHeader, tv_companyName;
    EditText profileFirstName,profileLastName, profileContact, profileAddress, profileEmail, profileDOB;
    String firstName, lastName, contact, address, email, dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("thisName");
            navEmailAddress = extras.getString("thisEmail");
            navProfilePicUri = extras.getString("ProfilePicUri");
        }

        Picasso.get().load(navProfilePicUri).into(cv_profilePicture);
        nameHeader.setText(navUserName);

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


                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(view.getContext());

                // Dialogbox visuals
                confirmationDialog.setTitle("Confirm Changes");
                confirmationDialog.setMessage("Please select to confirm changes");

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

                        Toast.makeText(EmployeeProfile.this, "Profile Information Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), EmployeeLanding.class);
                        intent.putExtra("thisName", navUserName);
                        intent.putExtra("thisEmail", navEmailAddress);
                        intent.putExtra("ProfilePicUri", navProfilePicUri);
                        startActivity(intent);
                    }
                });

                confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EmployeeProfile.this, "Update Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                confirmationDialog.create().show();
            }
        });

        //Profile pic upload
        cv_profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
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
            Toast.makeText(EmployeeProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
            return;
        }  if (TextUtils.isEmpty(lastName)) {
        profileLastName.setError("Last Name Required.");
        Toast.makeText(EmployeeProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
        return;
    }  if (TextUtils.isEmpty(contact)) {
        profileContact.setError("Contact Required.");
        Toast.makeText(EmployeeProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
        return;
    }  if (TextUtils.isEmpty(address)) {
        profileAddress.setError("Address Required.");
        Toast.makeText(EmployeeProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
        return;
    }  if (TextUtils.isEmpty(email)) {
        profileEmail.setError("Email Required.");
        Toast.makeText(EmployeeProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
        return;
    }  if (TextUtils.isEmpty(dateOfBirth)) {
        profileDOB.setError("Date of birth Required.");
        Toast.makeText(EmployeeProfile.this, "Error! Enter Missing Information", Toast.LENGTH_SHORT).show();
        return;
    }

        Intent intent = new Intent(getApplicationContext(), EmployeeLanding.class);
        intent.putExtra("thisName", navUserName);
        intent.putExtra("thisEmail", navEmailAddress);
        intent.putExtra("ProfilePicUri", navProfilePicUri);
        startActivity(intent);
    }

    //Profile pic upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            cv_profilePicture.setImageURI(imageUri);
        }
        if (imageUri != null)
        {
            uploadToFirebase(imageUri);
        } else {
            Toast.makeText(EmployeeProfile.this, "Please Select Image",Toast.LENGTH_LONG).show();
        }
    }

    //Profile pic upload
    private void uploadToFirebase(Uri uri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                                fStore.collection("Users").document(uid).set(updateData, SetOptions.merge());
                                Log.d("SavingURL", "Saving URl to database" + uri );
                                Log.d("uploadToFirebase", "Successful " );
                                //if image upload successful - show toast
                                Toast.makeText(EmployeeProfile.this, "Upload Successful",Toast.LENGTH_LONG).show();
                                Picasso.get().load(uri).into(cv_profilePicture);
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
                Toast.makeText(EmployeeProfile.this, "Upload Error",Toast.LENGTH_LONG).show();
            }
        });
    }


    // to get selected image extension : jpeg, png etc
    private String getFileExtension(Uri mUri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(mUri));
    }


}