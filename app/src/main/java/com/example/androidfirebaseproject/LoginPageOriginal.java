package com.example.androidfirebaseproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.employee.EmployeeLanding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginPageOriginal extends AppCompatActivity {
    //UI
    EditText inputEmail, inputPassword;
    String inputUserEmail, inputUserPassword;
    String userId;
    ImageView fingerprintscanner;

    // Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page_original);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        fingerprintscanner = findViewById(R.id.fingerprintview);
        TextView forgotPass = (TextView) findViewById(R.id.textForgotPassword);
        TextView SignUp = (TextView) findViewById(R.id.textSignUp2);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Forgot password button
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetPasswordEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());

                // Dialogbox visuals
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Email To Receive Reset Link.");
                resetPasswordEmail.setHint("Enter Email Here");
                passwordResetDialog.setView(resetPasswordEmail);
                // if OK
                passwordResetDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String recovery_email = resetPasswordEmail.getText().toString();

                        if (TextUtils.isEmpty(recovery_email))
                        {
                            resetPasswordEmail.setError("Invalid Email");
                            Toast.makeText(LoginPageOriginal.this, "Invalid Email", Toast.LENGTH_LONG).show();
                            return;
                        } else {

                            fAuth.sendPasswordResetEmail(recovery_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(LoginPageOriginal.this, "Reset Password Email Sent!", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginPageOriginal.this, "Password Reset Failure - Invalid Email", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                // If CANCEL
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginPageOriginal.this, "Password Reset Failed", Toast.LENGTH_LONG).show();
                    }
                });
                passwordResetDialog.create().show();
            }
        });


        // Signup button
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });

        //Fingerprint Scanner image
        fingerprintscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FingerprintScanner.class));
            }
        });
    }

    public void callSignin(View view) {
        // Getting user input as strings
        inputUserEmail = inputEmail.getText().toString().trim();
        inputUserPassword = inputPassword.getText().toString().trim();

            // Validating User Input
            if ( !(inputUserEmail.contains("@")) || !(inputUserEmail.contains(".com")) && !(inputUserEmail.contains(".edu")))
            {
                inputEmail.setError("Invalid Email!");
                return;
            }
            if (TextUtils.isEmpty(inputUserPassword))
            {
                inputPassword.setError("Password Required.");
                return;
            }

            // Getting user SignIn into app using firebase auth
            fAuth.signInWithEmailAndPassword(inputUserEmail,inputUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        // getting current user's email
                        userId = fAuth.getCurrentUser().getUid();
                        FirebaseUser user = fAuth.getCurrentUser();
                        Toast.makeText(LoginPageOriginal.this,"SignIn Successful!", Toast.LENGTH_SHORT).show();

                        // saving data in sharedpreference for fingerprint authentication
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("email",inputUserEmail );
                        editor.putString("password", inputUserPassword);
                        editor.putBoolean("isLogin",true);
                        editor.apply();

                        // Check if email is not verified, take user to confirm email activity
                        if (!user.isEmailVerified())
                        {
                            // if user email is not verified - goto com.myapplication.ConfirmEmail
                            Log.d("Email Verification", " Email Not Verified - Moving to com.myapplication.ConfirmEmail");
                            startActivity(new Intent(getApplicationContext(), ConfirmEmail.class));

                        } else {

                            Log.d("Email Verification", " Email Verified - Moving to Access Level Check");
                            // Checking user's access level
                            Log.d("User UID", " UID : " + fAuth.getCurrentUser().getUid());
                            checkUserAccessLevel(fAuth.getCurrentUser().getUid());
                        }

                    } else {
                        Toast.makeText(LoginPageOriginal.this,"SignIn Error! " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginPageOriginal.this,"Sign in Error! Account not found! ", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(view.getContext());
                    // Dialogbox visuals
                    confirmationDialog.setTitle("New To App? ");
                    confirmationDialog.setMessage("Please Select Yes To Create New Account");
                    confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(new Intent(getApplicationContext(), SignUp.class));
                        }
                    });
                    confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    confirmationDialog.create().show();
                }
            });
    }

    // checking user access level
    private void checkUserAccessLevel(String userId) {
        // creating reference to user's data in database using userid = UID;
        DocumentReference docReference = fStore.collection("Users").document(userId);
        docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("User Access Level", " On Success: " + documentSnapshot.getData());

                // If employee is terminated, call activity to display msg and keep employee out of main app
                if (documentSnapshot.getString("isAdmin").equals("terminated"))
                {
                    startActivity(new Intent(getApplicationContext(),TerminatedEmployee.class));
                    return;
                }
                // if employee is active, check access layer
                if (documentSnapshot.getString("isAdmin").equals("admin"))
                {
                    // If admin - goto Admin homepage
                    Log.d("User Access Level", " User is Admin - Moving to Admin Homepage");
                   startActivity(new Intent(getApplicationContext(),Admin_Homepage.class));
                   // startActivity(new Intent(getApplicationContext(), FeedingData.class));

                } else {
                    // If not admin - goto User homepage
                    Log.d("User Access Level", " User is Not Admin - Moving to User Homepage");
              startActivity(new Intent(getApplicationContext(), EmployeeLanding.class));
                   //startActivity(new Intent(getApplicationContext(), FeedingData.class));
                }
            }
        });
    }
}