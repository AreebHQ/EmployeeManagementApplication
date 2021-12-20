package com.example.androidfirebaseproject;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.employee.EmployeeLanding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class FingerprintScanner extends AppCompatActivity {

    private static final int REQUEST_CODE =101000 ;
    ImageView fingerprintView;
    SharedPreferences sharedPreferences;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    String userId;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_scanner);
        fingerprintView = findViewById(R.id.fingerPrintImageView);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("FingerprintScanner", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("FingerprintScanner", "No biometric features available on this device.");
                Toast.makeText(this, "Fingerprint sensor not available.", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("FingerprintScanner", "Biometric features are currently unavailable.");
                Toast.makeText(this, "Fingerprint sensor is busy or not available.", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }


        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintScanner.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d("FingerprintScanner", "Authentication Error");
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Log.d("FingerprintScanner", "Authentication Successful");
                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                String email = sharedPreferences.getString("email","");
                String password = sharedPreferences.getString("password","");

                // Getting user SignIn into app using firebase auth
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // getting current user's email
                            userId = fAuth.getCurrentUser().getUid();
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(FingerprintScanner.this,"SignIn Successful!", Toast.LENGTH_SHORT).show();
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
                          }
                        else {
                            Toast.makeText(getApplicationContext(),"SignIn Error! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d("FingerprintScanner", "Authentication Failed");
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("FingerPrint login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        fingerprintView.setOnClickListener(view -> { biometricPrompt.authenticate(promptInfo);  });
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
                // Check if isAdmin field is set to [ 1 = User is Admin ] [ 0 = User is Not Admin]
                if (documentSnapshot.getString("isAdmin").equals("admin"))
                {
                    // If admin - goto Admin homepage
                    Log.d("User Access Level", " User is Admin - Moving to Admin Homepage");
                    startActivity(new Intent(getApplicationContext(),Admin_Homepage.class));

                } else {
                    // If not admin - goto User homepage
                    Log.d("User Access Level", " User is Not Admin - Moving to User Homepage");
                    startActivity(new Intent(getApplicationContext(), EmployeeLanding.class));
                }
            }
        });

    }
}