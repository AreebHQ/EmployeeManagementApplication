package com.example.employee;


import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class scannerView extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    ZXingScannerView scannerView;

    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    boolean isClockIn = false;

    // Timer Time
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
    // Clock In time
    DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    DateFormat checkDay = new SimpleDateFormat("dd");
    DateFormat checkMonth = new SimpleDateFormat("MM");
    DateFormat checkYear = new SimpleDateFormat("yyyy");
    DateFormat checkTime = new SimpleDateFormat("K:mm a");
    Date currentDate = new Date();
    // Getting Month Year and Time separately
    String strMon;
    String strYear;
    String strTime;
    String strDay;
    TextView attnStatus;
    String monthName;
    String attnCode;
    String companyCode;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            companyCode = extras.getString("companyCode");

        }

        // Getting Month Year and Time separately
        strMon = checkMonth.format(currentDate);
        strYear = checkYear.format(currentDate);
        strTime = checkTime.format(currentDate);
        strDay = checkDay.format(currentDate);
        monthName = months[Integer.parseInt(strMon)];

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(scannerView.this, "Camera Authorization Error! \n Please allow to user camera and try again", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void handleResult(Result rawResult) {
        QRscannerAttendance.tv_ssid.setText(rawResult.getText());
        attnCode = rawResult.getText().trim();
        checkCode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }



    private void checkCode() {

        Date currentDate = new Date();
        // Getting date ex: 11-22-2021
      //  DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        // Getting month name from month array
       // String monthName = months[Integer.parseInt(strMon)];
        String date = dateFormat.format(currentDate);

        DocumentReference docRef = fStore.collection("Users").document(companyCode)
                .collection("QRCodeAttendance").document(strYear).collection(monthName).document(date);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String getCode = document.getString("QRValue");
                        Log.d("Admin QR Code", getCode);
                        Log.d("UserScan QR Code", attnCode);
                        if (!TextUtils.isEmpty(getCode)) {
                            if (getCode.equals(attnCode)) {
                                clockIn();
                                startActivity(new Intent(getApplicationContext(), EmployeeClock.class));
                            } else {
                                Toast.makeText(scannerView.this, "QR Code Error! \n Please check the QR Code and try again", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), QRscannerAttendance.class));
                            }
                        }
                    } else {
                        Toast.makeText(scannerView.this, "QR Code Error! \n QR Data not found - Contact your Administrator", Toast.LENGTH_LONG).show();
                        Log.d("QR Code", "Failed with ", task.getException());
                    }

                } else {
                    Log.d("QR Code", "Failed with ", task.getException());
                }
            }
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
        String date = dateFormat.format(currentDate);

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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","NOT SAVED");
                Toast.makeText(getApplicationContext(), "Clock In Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
