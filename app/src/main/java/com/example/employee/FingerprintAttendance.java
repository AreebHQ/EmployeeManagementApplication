package com.example.employee;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.androidfirebaseproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class FingerprintAttendance extends AppCompatActivity {
    private static final int REQUEST_CODE =101000 ;
    ImageView fingerprintView;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    TextView attnStatus;

    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    boolean isClockIn = false;

    // Timer Time
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
    Date systemTime = Calendar.getInstance().getTime();
    // Clock In time
    DateFormat checkDay = new SimpleDateFormat("dd");
    DateFormat checkMonth = new SimpleDateFormat("MM");
    DateFormat checkYear = new SimpleDateFormat("yyyy");
    DateFormat checkTime = new SimpleDateFormat("K:mm a");
    Date date2 = new Date();
    // Getting Month Year and Time separately
    String strMon;
    String strYear;
    String strTime;
    String strDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_attendance);
        fingerprintView = findViewById(R.id.fingerPrintImageView);
        attnStatus = findViewById(R.id.attendanceStatus);

        Intent intentReceiver = getIntent();
        isClockIn = intentReceiver.getBooleanExtra("clockInStatus",false);
        // Reversing the clockstatus which we got from home activity
        if (isClockIn)
        {
            isClockIn = false;
        } else {
            isClockIn = true;
        }

        // Now its right status - setting as per status
        if (isClockIn)
        {
            isClockIn = false;
            attnStatus.setText("Clocked Out");
            attnStatus.setTextColor(Color.RED);
        } else {
            isClockIn = true;
            attnStatus.setText("Clocked In");
            attnStatus.setTextColor(Color.GREEN);

        }

        // Getting Month Year and Time separately
         strMon = checkMonth.format(date2).toString();
         strYear = checkYear.format(date2).toString();
         strTime = checkTime.format(date2).toString();
         strDay = checkDay.format(date2).toString();


        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(getApplicationContext(), "Application Can User Biometric Authentication", Toast.LENGTH_SHORT).show();
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

        // Checking clock in status
       // checkTimePunch(strYear, strMon);


        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintAttendance.this,
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
                Toast.makeText(getApplicationContext(), "Authentication succeeded! - Time Punch Entered Successfully", Toast.LENGTH_SHORT).show();

               if (isClockIn)
               {
                   isClockIn = false;
                   Toast.makeText(getApplicationContext(), "Authentication succeeded! - Time Punch Entered Successfully \n User Clocked Out", Toast.LENGTH_SHORT).show();
                   clockOut();

               } else {
                   isClockIn = true;
                   Toast.makeText(getApplicationContext(), "Authentication succeeded! - Time Punch Entered Successfully \n User Clocked In", Toast.LENGTH_SHORT).show();
                   clockIn();
               }
               startActivity(new Intent(getApplicationContext(), EmployeeClock.class));
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


        fingerprintView.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }



    // pushing clock in time into firebase database
    public void clockIn()
    {

        // Timer Time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date systemTime = Calendar.getInstance().getTime();
        String timerStartTime = sdf.format(systemTime);

        // Checking the right items
        Log.d("Year","Year is: "+ strYear);
        Log.d("Month","Month is: "+ strMon);
        Log.d("Time","Time is: "+ strTime);
        Log.d("Day","Time is: "+ strDay);



        //timeFormat = new SimpleDateFormat("MM-dd-yyyy K:mm a");

        // Getting date ex: 11-22-2021
        DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = dateFormat.format(date2).toString();

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        // Setting month name and year as firebase document name ex: nov2021, jan2021


        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid())
                .collection("Attendance").document(strYear).collection(monthName).document(date);

        //Storing user info in map
        Map<String, Object> punchInfo = new HashMap<>();
        punchInfo.put("Date", date);
        punchInfo.put("ClockIn",strTime);
        punchInfo.put("Day",strDay);
        punchInfo.put("TimerStart",timerStartTime);

        docReference.set(punchInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Successful","Saved Date: "+ date + " Time: "+ strTime);
                Log.d("Successful","Saved Time: "+ date + " Time: "+ timerStartTime);
                Toast.makeText(getApplicationContext(), "Clocked In Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), EmployeeClock.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","Clock In Failed!");
                Toast.makeText(getApplicationContext(), "Clock In Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // pushing clock out time in firebase database
    public void clockOut()
    {
        // Timer Time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date systemTime = Calendar.getInstance().getTime();
        String timerStopTime = sdf.format(systemTime);


        DateFormat checkMonth = new SimpleDateFormat("MM");
        DateFormat checkYear = new SimpleDateFormat("yyyy");
        DateFormat checkTime = new SimpleDateFormat("K:mm a");
        Date date2 = new Date();

        // Getting Month Year and Time separately
        String strMon= checkMonth.format(date2).toString();
        String strYear = checkYear.format(date2).toString();
        String strTime = checkTime.format(date2).toString();


        // Checking the right items
        Log.d("Year","Year is: "+ strYear);
        Log.d("Month","Month is: "+ strMon);
        Log.d("Time","Time is: "+ strTime);


        //timeFormat = new SimpleDateFormat("MM-dd-yyyy K:mm a");

        // Getting date ex: 11-22-2021
        DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = dateFormat.format(date2).toString();

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        // Setting month name and year as firebase document name ex: nov2021, jan2021

        String hours = EmployeeClock.timerTime.replaceAll("\\s+", "");

        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid())
                .collection("Attendance").document(strYear).collection(monthName).document(date);

        //Storing user info in map
        Map<String, Object> punchInfo = new HashMap<>();
        punchInfo.put("ClockOut",strTime);
        punchInfo.put("TimerStop",timerStopTime);
        punchInfo.put("Hours",hours);

        // Saving / Merging data with the existing clock in punch
        docReference.set(punchInfo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Successful","Saved Date: "+ date + " Time: "+ strTime);
                Log.d("Successful","Saved Time: "+ date + " Time: "+ timerStopTime);
                Toast.makeText(getApplicationContext(), "Clocked Out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), EmployeeClock.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","NOT SAVED=======================");
                Toast.makeText(getApplicationContext(), "Clock Out Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}